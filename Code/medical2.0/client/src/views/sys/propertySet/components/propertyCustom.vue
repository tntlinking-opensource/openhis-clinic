<template>
  <div>
    <el-row style="margin-bottom: 10px">
      <el-col :span="21">
        <div style="font-weight: 600;">属性定义</div>
      </el-col>
      <el-col :span="2">
        <el-button type='primary' :disabled="action == 'view'" @click="pushCustomTeam">新增分组</el-button>
      </el-col>
    </el-row>
    <el-collapse ref="collapseTrigger">
      <el-collapse-item :name="bizindex" v-for="(panel, bizindex) in propertyPanels" :key="bizindex" >
        <template slot="title" v-if="action !== 'view' && panel.isTeam !== '0'">
          <template @click.stop="collClick" >
            <i class="el-icon-remove-outline" @click.stop="deletePropertyData(bizindex, panel, propertyPanels)"></i>
            <el-form-item :data-num='dataNum' :ref="`PRO_${dataNum}_propertyPanels_${bizindex}_name`" style="margin-top: 16px" v-if='action !== "view" && panel.isTeam !== "0"' :prop='`PRO_${dataNum}_propertyPanels_${bizindex}_name`' :rules="[{ required: true, validator: inputValidator, val: panel.name,  message: ' ', trigger: 'blur'}]" >
              <el-input v-model="panel.name" @focus="nameFocus" @blur="nameBlur"  placeholder="请输入内组名"/>
            </el-form-item>
          </template>
        </template>
        <template slot="title" v-else>
          <span style="margin-left: 30px;">{{panel.name ? panel.name : '默认分组'}}</span>
        </template>
        <el-table   class='drag_table'   row-key="id" ref="dragTable" :data='panel.propertys' border highlight-current-row :row-class-name="tableRowClassName">
          <el-table-column prop='name' label='字段名称'  header-align='center'>
            <template slot='header' slot-scope='{row,$index}'><span style="color:#F56C6C;margin-right: 4px">*</span>字段名称</template>
            <template slot-scope='{row,$index}'>
              <el-form-item :data-num='dataNum' :ref="`PRO_${dataNum}_propertys_${$index}_name`" :prop='`PRO_${dataNum}_propertys_${$index}_name`' :rules="[{ required: true, validator: inputValidator, val: row.name,  message: '请输入字段名称', trigger: 'blur'}]"  v-if='action !== "view" '  label-width=0>
                <el-input v-model="row.name" />
              </el-form-item>
              <span v-else >{{row.name}}</span>
            </template>
          </el-table-column>
          <el-table-column prop="dictId" label="属性"  header-align='center'>
            <template slot='header'><span style="color:#F56C6C;margin-right: 4px">*</span>属性</template>
            <template slot-scope='{row,$index}'>
              <el-form-item :data-num='dataNum' label-width=0 v-if='action !== "view" ' :ref="`PRO_${dataNum}_propertys_${$index}_dictId`" :prop='`PRO_${dataNum}_propertys_${$index}_dictId`' :rules="[{ required: true, validator: inputValidator, val: row.dictId,  message: '请选择属性集', trigger: ['blur', 'change']}]">
                <el-select v-model="row.dictId" value-key='id' filterable clearable placeholder='请选择属性类型' @change="changeDictId(row)">
                  <el-option v-for="item in options" :key="item.id" :label="item.name" :value="item.id" :disabled="item.disabled"/>
                </el-select>
              </el-form-item>
              <span v-else>{{getCheckedName(row.dictId, options)}}</span>
            </template>
          </el-table-column>
          <el-table-column prop="bo" label="业务对象" width="100" header-align='center'>
            <template slot-scope='{row,$index}'>
              <el-form-item :data-num='dataNum' label-width=0  v-if='action !== "view" ' :ref="`PRO_${dataNum}_propertys_${$index}_bo`" :prop='`PRO_${dataNum}_propertys_${$index}_bo`' :rules="{required: (row.dictId == '8606' || row.dictId == '8406' || row.dictId == '8405') ? true : false, validator: inputValidator, val: row.bo, message:'请选择业务对象',trigger: ['blur', 'change']}">
                <el-select v-model="row.bo" value-key='id' v-if="row.dictId === '8406'" filterable @change="(val) => getDictChildren(val,row)" clearable placeholder='请选择业务对象' >
                  <el-option v-for="item in businessOptions" :key="item.id" :label="item.name" :value="item.id" :disabled="item.disabled"/>
                </el-select>
                <el-select v-model="row.bo" value-key='id' v-else-if="row.dictId === '8606'" filterable clearable placeholder='请选择属性类型' >
                  <el-option v-for="item in propertySetS" :key="item.id" :label="item.name" :value="item.id" :disabled="item.disabled"/>
                </el-select>
                <el-input v-model="row.bo" v-else-if="row.dictId === '8405'" />
                <el-input v-model="row.bo" v-else :disabled="true" />
              </el-form-item>
              <span v-else-if="row.dictId === '8406'">{{getCheckedName(row.bo,businessOptions)}}</span>
              <span v-else-if="row.dictId === '8606'">{{getCheckedName(row.bo,propertySetS)}}</span>
              <span v-else>{{row.bo}}</span>
            </template>
          </el-table-column>
          <el-table-column prop='isRequired' label='是否必填' width="80" header-align='center'>
            <template slot-scope='{row,$index}'>
              <el-form-item :data-num='dataNum' v-if='action !== "view" ' label-width=0>
                <el-switch v-model='row.isRequired' active-color='#13ce66' inactive-color='#dbdfe6' active-value='1' inactive-value='0'></el-switch>
              </el-form-item>
              <li v-else-if='row.isRequired == "1"' class='el-icon-check' style='color:#F56C6C;'></li>
            </template>
          </el-table-column>
          <el-table-column prop="defaultValue" label="默认值"  header-align='center' >
            <template slot-scope='{row,$index}'>
              <template v-if="row.dictId == '8406'"> <!-- 字典 -->
                <el-form-item :data-num='dataNum' v-if='action !== "view" ' label-width=0>
                  <el-select :loading="defLoading" v-model="row.defaultValue" value-key='id' filterable clearable placeholder='请选择属性类型' @focus="checkChange(row)" >
                    <el-option v-for="item in defValOptions" :key="item.id" :label="item.name" :value="item.id" :disabled="item.disabled"/>
                  </el-select>
                </el-form-item>
                <span v-else>{{getCheckedName(row.defaultValue,defValOptions)}}</span>
              </template>

              <template v-else-if=" row.dictId == '8402' || row.dictId == '8403'">     <!--  8402整数  "8403" 数值 -->
                <el-form-item :data-num='dataNum'  v-if='action !== "view" ' label-width=0>
                  <number-input  v-model='row.defaultValue' :precision="row.dictId == '8402'? 0 : 4"></number-input>
                </el-form-item>
                <span v-else>{{row.defaultValue}}</span>
              </template>

              <template v-else-if="row.dictId == '8404'">     <!--  8404 金额 -->
                <el-form-item :data-num='dataNum'  v-if='action !== "view" ' label-width=0>
                  <number-input  v-model='row.defaultValue' :precision="2"></number-input>
                </el-form-item>
                <span v-else>{{row.defaultValue}}</span>
              </template>

              <template v-else>
                <el-form-item :data-num='dataNum'  v-if='action !== "view" ' label-width=0>
                  <el-input v-model="row.defaultValue" />
                </el-form-item>
                <span v-else>{{row.defaultValue}}</span>
              </template>
            </template>
          </el-table-column>
          <el-table-column prop='remarks' label='说明'  header-align='center'>
            <template slot-scope='{row,$index}'>
              <el-form-item :data-num='dataNum' v-if='action !== "view" ' label-width=0>
                <el-input v-model='row.remarks' :maxlength='128' clearable placeholder='请输入说明'></el-input>
              </el-form-item>
              <span v-else>{{row.remarks}}</span>
            </template>
          </el-table-column>
          <el-table-column label='操作' fixed='right' min-width="40px" v-if="action !== 'view'" >
            <template slot='header' slot-scope="scope">
              <el-button circle icon="el-icon-plus" :disabled="action === 'view'" @click="pushPropertyData(panel, bizindex)"></el-button>
            </template>
            <template slot-scope='scope'>
              <OperationIcon type='danger' content='删除' placement='top-start' icon-name='el-icon-delete'
                             @click='onDelete(scope.$index, scope.row, panel.propertys)'></OperationIcon>
            </template>
          </el-table-column>
        </el-table>
      </el-collapse-item>
    </el-collapse>
  </div>
</template>

<script>
  import { listDictItemAll } from '@/api/sys/dictItem'
  import { listDictTypeAll } from '@/api/sys/dictType'
  import { listPropertySetAll } from  "@/api/sys/propertySet"
  import OperationIcon from '@/components/OperationIcon'
  import { uuid } from 'vue-uuid'
  import Sortable from "sortablejs";

  export default {
    name: "propertyCustom",
    components:{OperationIcon},
    props:{
      value:{
        type:String
      },
      //操作类型--编辑、查看等
      action: {
        type: String,
        default: () => { return "add"}
      },
      dataNum: {
        type: String | Number,
        required: true
      }
     },
    watch:{
      // 关闭窗口后，在此打开时触发
      value(val, oldVal) {
        if(val != oldVal) {
          if(this.value) {
            this.propertyPanels = JSON.parse(this.value)
          } else {
            this.propertyPanels = this.initPropertyDef()
          }
        }
      },
      propertyPanels: {
        handler(newName, oldName) {
          this.$emit('input', JSON.stringify(this.propertyPanels))
        },
        deep: true
      },
      'action':{
        handler(newVal){
          if(newVal != 'view') {
            this.getSortable()
          }
        },
      }
    },
    data(){
      let inputValidator= (rule, value, callback) => {
        if(rule.required) {
          if(!rule.val) {
            let msg = rule.message | '不能为空！'
            return callback(new Error(msg))
          }
        }
        callback()
      }
       return{
         propertyPanels:this.initPropertyDef(),
         deleteTeamArr:[], //要删除的自定义属性分组
         deleteArr:[],     //要删除的自定义属性id
         indexArr:[],  //展开卡片得下标数组
         tableKey: 1,
         checkRowData:null, // 选中行数据
         options:[],        //字典值
         businessOptions: [], //所有字典项
         propertySetS:[],        //属性集集合
         defValOptions: [],   //选择属性的字典
         dictIdvalid: false,
         defLoading: false,
         inputValidator: inputValidator,
       }
     },
      mounted() {
        // 初始值
        if(this.value) {
          this.propertyPanels = JSON.parse(this.value)
        }
        this.getOptions();
        if (this.action != 'view') {
          this.getSortable()
        }
      },
    methods:{
      getSortable() {
        for(let index in this.propertyPanels){
          this.$nextTick(() =>{
            this.initSortable(index,this.propertyPanels[index].propertys);
          });
        }
      },
      initSortable(index,tableData) {
        // 获取表格row的父节点
        const ele = this.$refs["dragTable"][index].$el.querySelector(
          ".el-table__body-wrapper tbody"
        );

        // 创建拖拽实例
        let dragTable = Sortable.create(ele, {
          animation: 100, //动画
          disabled: false, // 拖拽不可用? false 启用（刚刚渲染表格的时候起作用，后面不起作用）
          handle: ".el-table__row", //指定拖拽目标，点击此目标才可拖拽元素(此例中设置操作按钮拖拽)
          filter: ".disabled", //指定不可拖动的类名（el-table中可通过row-class-name设置行的class）
          dragClass: "dragClass", //设置拖拽样式类名
          ghostClass: "ghostClass", //设置拖拽停靠样式类名
          chosenClass: "chosenClass", //设置选中样式类名
          // 开始拖动事件
          onStart: () => {
            //console.log("开始拖动");
          },
          // 结束拖动事件
          onEnd: ({newIndex, oldIndex}) => {
              const row = this.propertyPanels[index].propertys.splice(oldIndex,1)[0];
              this.propertyPanels[index].propertys.splice(newIndex,0,row);
          },
        });
      },
      nameFocus() {
        this.collClick = this.$refs.collapseTrigger.setActiveNames;
        this.$refs.collapseTrigger.setActiveNames = () => {}
      },
      nameBlur() {
        this.$refs.collapseTrigger.setActiveNames = this.collClick
      },
      onDelete(index,row,list){     //删除
        list.splice(index,1);
      },
      getCheckedName(id,arr){
        let name = "";
        if(id && arr){
          arr.some((item, index) => {
            if(item.id === id){
              name = item.name;
              return true
            }
          })
        }
        return name;
      },
      // 触发默认值下拉框时加载对应选项
      checkChange(currentRow){
        this.checkRowData = currentRow;
        if(this.action !== "view" ){
          if(currentRow.dictId === '8406'){
            let dictItem = {
              params: [{'columnName':'dict_type_id', 'queryType': '=', 'value': currentRow.bo}]
            };
            this.defLoading = true;
            listDictItemAll(dictItem).then(responseData => {
              this.defValOptions = responseData.data;
              this.defLoading = false
            })
          }
        }
      },
      //获取字典项的子集
      getDictChildren(val,row){
        row.defaultValue = null;
        let dictItem = {
          params: [{'columnName':'dict_type_id', 'queryType': '=', 'value': val}]
        };
        listDictItemAll(dictItem).then(responseData => {
          this.defValOptions = responseData.data;
        })
      },
      //获取字典数据
      getOptions(){
        let dictItem = {
          params: [{'columnName':'dict_type_id', 'queryType': '=', 'value': '5100'}]
        };
        listDictItemAll(dictItem).then(res => {
          if(res.code === "100"){
            this.options = res.data;
          }
        });
        listDictTypeAll({params: []}).then(result =>{  //所有字典项
          if(result.code === "100"){
            this.businessOptions = result.data
          }
        });
        listPropertySetAll({params: []}).then(result =>{  //所有属性集
          if(result.code === "100"){
            this.propertySetS = result.data
          }
        });

      },
      changeDictId(row) {  // {属性集  8606}  {字典项  8406}  {枚举  8405}
        row.bo = "";
        row.defaultValue = null;
      },
      pushCustomTeam(){       //新增分组
        this.propertyPanels.push({'id':uuid.v1(),'name':'','isTeam': '1','propertys': []},)
      },
      deletePropertyData(index,row,list) { // 删除分组
        list.splice(index,1);
        if(row.id){
          this.deleteTeamArr.push({id:row.id});
        }
        if(this.indexArr.indexOf(index) !== -1){
          this.indexArr.splice(this.indexArr.indexOf(index),1);
        }
      },
      isIndexOfArr(val){
        return this.indexArr.indexOf(val) !== -1;
      },
      indexChange(bizindex){
        if(this.indexArr.indexOf(bizindex) === -1){
          this.indexArr.push(bizindex);
        }else{
          this.indexArr.splice(this.indexArr.indexOf(bizindex),1)
        }
      },
      //新增扩展属性
      pushPropertyData(panel, index){
        if(this.indexArr.indexOf(index) === -1){
          this.indexArr.push(index);
        }
        this.propertyPanels[index].propertys.push(
          {'id':uuid.v1(),
          'name':'',
          'dictId': null,
          'bo': "",
          'defaultValue': null,
          'isRequired': "0",
          'remarks':'',
          'edit': true,
        });
        this.tableKey++
      },
      tableRowClassName({row, rowIndex}) {
        row.index = rowIndex
        if (row == this.checkRowData) {
          return 'current-row'
        }
      },
      //默认初始化属性定义
      initPropertyDef(){
        return [
          {'id':uuid.v1(),'name':'','isTeam': '0','propertys': []}
        ];
      },
    }
  }
</script>

<style lang="scss" >
  // 拖拽
  .dragClass {
    background: rgba($color: #41c21a, $alpha: 0.5) !important;
  }
  // 停靠
  .ghostClass {
    background: rgba($color: #6cacf5, $alpha: 0.5) !important;
  }
  // 选择
  .chosenClass:hover > td {
    background: rgba($color: #f56c6c, $alpha: 0.5) !important;
  }
/deep/ .el-form-item__content {
  margin-top: 0px;
}
.dialog-footer {
.btnGroup {
    position: absolute;
    left: 45%;
  }
}
/deep/ .el-badge {
  display: block;
}
.el-icon-remove-outline {
    font-size: 20px;
    color: #F56C6C;
    margin-right: 5px;
}
</style>
