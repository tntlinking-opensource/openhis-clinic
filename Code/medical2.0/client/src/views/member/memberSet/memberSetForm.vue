<template>
  <el-dialog :title='dialogProps.title' :visible.sync='dialogProps.visible' :close-on-click-modal='false' width='50%' 
    @open='onDialogOpen()' v-loading='loading'>
    <div slot='title' class='dialog-header'>
      {{ dialogProps.title }}
      <!-- <OperationIcon v-show='dialogProps.action == "view" && permission.edit' type='primary' text='编辑' placement='top-start' icon-name='el-icon-edit' @click='switchEdit'></OperationIcon> -->
    </div>

    <el-form :model='bizFormModel' :rules='formRules' 
      ref='memberSetForm' label-width='120px' label-position='right' class='edit-form'>  
      <div class="tab-item" v-show='tabIndex=="1"'>  
              <el-row>
                <el-col :span='24/2'>
          <el-form-item label='会员卡名称' prop='name' >
            <el-input :disabled='dialogProps.action == "view"' v-model='bizFormModel.name' :maxlength='255' :placeholder='dialogProps.action == "view"? "" : "请输入会员卡名称"' ></el-input>
          </el-form-item>
        </el-col>
         <el-col :span='24/2'>
          <el-form-item label='会员卡张数' prop='amount' >
            <el-input :disabled='dialogProps.action == "view"' oninput="value=value.replace(/[^\d.]/g,'')"  v-model='bizFormModel.amount'></el-input>
          </el-form-item>
        </el-col>
      </el-row>
              <el-row>
        <el-col :span='24/2'>
          <el-form-item label='会员卡类型' prop='type' >
            <!-- <el-input :disabled='dialogProps.action == "view"' v-model='bizFormModel.type' :maxlength='100' :placeholder='dialogProps.action == "view"? "" : "请输入会员卡类型"' ></el-input> -->
                            <el-select
                              v-model='bizFormModel.type'
                             :disabled='dialogProps.action == "view"'
                             :placeholder='dialogProps.action == "view"? "" : "请选择会员卡类型"'
                            >
                              <el-option
                                v-for="item in memberType"
                                :key="item.value"
                                :label="item.name"
                                :value="item"
                              >
                              </el-option>
                            </el-select>
          </el-form-item>
        </el-col>
         <el-col :span='24/2'>
           
          <el-form-item label='有效时间' prop='startDate' >
            <!-- <el-date-picker :disabled='dialogProps.action == "view"' v-model='bizFormModel.startDate' type='date' value-format='yyyy-MM-dd HH:mm:ss' :placeholder='dialogProps.action == "view"? "" : "请输入开始时间"' ></el-date-picker>              -->
            <el-date-picker
            v-model="bizFormModel.startDate"
            type="daterange"
            style="width:290px"
            :disabled='dialogProps.action == "view"' 
            value-format='yyyy-MM-dd HH:mm:ss' 
            :placeholder='dialogProps.action == "view"? "" : "请输入有效时间"'
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            :default-time="['00:00:00', '23:59:59']">
          </el-date-picker>
          </el-form-item>
          
        <!-- </el-col>
                <el-col :span='24/3'>
          <el-form-item label='结束时间' prop='endDate' >
            <el-date-picker :disabled='dialogProps.action == "view"' v-model='bizFormModel.endDate' type='date' value-format='yyyy-MM-dd HH:mm:ss' :placeholder='dialogProps.action == "view"? "" : "请输入结束时间"' ></el-date-picker>             
          </el-form-item>  -->
        </el-col>
      </el-row>
              <el-row>
        <el-col :span='24/2'>
          <el-form-item label='是否启用' prop='status' >
            <el-switch :disabled='dialogProps.action == "view"' v-model='bizFormModel.status' active-color='#13ce66' inactive-color='#dbdfe6' active-value='0' inactive-value='1'></el-switch>
          </el-form-item>
        </el-col>
             
      </el-row>
          <!-- 添加项目 -->
        <div>
            <el-row>
        <el-col :span='24'>
          <div>
            <span style="margin-left:55px;"><b>体验项目</b></span>
             <el-button v-if="dialogProps.action != 'view'" style="float:right;margin-top:-10px" type="primary" @click="addItem">添加体验项目</el-button>
          </div>
           </el-col>
      </el-row>
           <el-row>
             <el-col :span="24">
                 <el-table
              :data="memberData"
              style="width: 90%;margin-left:65px">
                <el-table-column
              label="序号"
              type="index"
              align="center">
            </el-table-column>     
              <el-table-column
                prop="name"
                label="项目名称"
                width="180">
                 <template slot-scope="scope">
                  <el-select
                    v-model='memberData[scope.$index].name'
                  :disabled='dialogProps.action == "view"'
                  :placeholder='dialogProps.action == "view"? "" : "请选择项目"'
                  @change="itemChange(scope.$index)"
                  >
                    <el-option
                      v-for="item in TreatmentTable"
                      :key="item.id"
                      :label="item.itemName"
                      :value="item.id"
                    >
                    </el-option>
                  </el-select>
              </template>
              </el-table-column>
             
              <el-table-column
                prop="itemType"
                label="项目类型"
                width="100">
              </el-table-column>
              <el-table-column
               align="center"
                prop="salePrice"
                label="项目金额">
                <template slot-scope="scope">
                  <span v-if='scope.row.salePrice>0'>
                    {{scope.row.salePrice}}元
                  </span>
                </template>
              </el-table-column>
               <el-table-column
                prop="number"
                label="项目数量"
                align="center">
                 <template slot-scope="scope">
                  <el-input-number :disabled="dialogProps.action == 'view'" size="mini" v-model="memberData[scope.$index].number" @change="handleChange"    ></el-input-number>
                  </template>
              </el-table-column>
              <el-table-column
                prop="unit"
                label="单位">
              </el-table-column>
             
                <el-table-column
                v-if="dialogProps.action != 'view'"
                  fixed="right"
                  label="操作"
                  width="100">
                  <template slot-scope="scope">
                    <el-button v-if="dialogProps.action != 'view'" @click="removeClick(scope.row)" type="text" size="small">删除</el-button>
                  </template>
                </el-table-column>
            </el-table>
             </el-col>
           </el-row>
        </div>
      </div>
    </el-form>
    <span slot='footer' class='dialog-footer'>
      <el-button v-if='dialogProps.action != "view"' type='primary' :plain='true' @click='onSubmit("memberSetForm")'>保 存</el-button>
      <el-button v-if='dialogProps.action != "view"' :plain='true' @click='onDialogClose()'>取 消</el-button>
      <el-button v-if='dialogProps.action == "view"' :plain='true' @click='onDialogClose()'>关 闭</el-button>
    </span>    
  </el-dialog>
</template>
<script>
import { validatenull } from '@/utils/validate'
import { listDictItemAll } from "@/api/sys/dictItem";
import { listCompanyAll } from '@/api/org/company'
import { saveMemberSet } from '@/api/member/memberSet'
import BaseUI from '@/views/components/baseUI'
import { listCostItemAll } from "@/api/treatment/costItem";
import OperationIcon from '@/components/OperationIcon'
import { BigNumber } from "bignumber.js";
export default {
  extends: BaseUI,
  name: 'memberSet-form',
  components: {
    OperationIcon
  },  
  data() {
    return {
      bizFormModel: this.initFormModel(),
      tabIndex: '1',
          company_List: [],  // 诊所id
       dialogProps: {
        visible: false,
        action: '',
        title: ''
      },
      TreatmentTable:[],   //诊疗项目集合
      formRules: {
        'name': [
            { required: true, message: '请输入会员卡名称', trigger: 'blur' }
        ],
        'type': [
            { required: true, message: '请输入会员卡类型', trigger: 'blur' }
        ],
        amount:[
          {required: true, message: '请输入会员卡张数', trigger: 'blur'}
        ]
      }
    }    
  },
  memberType:[],   //会员卡类型
  memberData:[
    {
      name:'',//项目名称
      itemType:'',  //项目类型
      salePrice:10,   //项目金额
      unit:"" ,  //单位
      number:0  //赠送数量
      
    }
  ],
  props: {
    // 权限
    permission: {
      type: Object
    }
  },  
  methods: {

    //进行项目删除
    removeClick(row,index){
      this.memberData.splice(index,1)
    },
    //添加项目
    addItem(){
      let objectItem={
        name:'',//项目名称
        itemType:'',  //项目类型
        salePrice:0,   //项目金额
        unit:"" ,  //单位
        number:""  //赠送数量
      }
      this.memberData.push(objectItem)
    },

    //选择诊疗项目
    itemChange(index){
      this.TreatmentTable.forEach(item=>{
        if(item.id==this.memberData[index].name){
          this.memberData[index].salePrice=item.salePrice
          this.memberData[index].itemType=item.itemType.name
          this.memberData[index].unit=item.unit.name
          this.memberData[index].number=1
        }
      })
    },
    //获取诊疗项目
    getCostItem(){
      let SearchModel = {
        columnName: "",
        limit: 10,
        offset: 0,
        order: "",
        params: [
          {
            columnName: "company_id",
            queryType: "=",
            value: currentUser.company.id,
          },
          {
            columnName: "item_name",
            queryType: "like",
            value: "",
          },
          {
            columnName: "item_type",
            queryType: "<>",
            value: "treatmentItemType_4",
          },
        ],
      };
      listCostItemAll(SearchModel).then((responseData) => {
        if (responseData.code == 100) {
          this.TreatmentTable = responseData.data;
        }
      });
    },
    //获取会员卡类型
    getType(){
      let model = {
        params: [
          {
            columnName: "dict_type_id",
            queryType: "=",
            value: '1224037951067242497',
          },
        ],
      };
      listDictItemAll(model).then((responseData) => {
          if(responseData.code=='100'){
            
            this.memberType=responseData.data
            console.log(this.memberType,'会员卡类型');
            this.$forceUpdate()
          }
      })

    },
    onSubmit(formName) {
      this.$refs[formName].validate(valid => {
        if (valid) {
          this.doSave()
        } else {
          return false
        }
      });
    },
    doSave() {
     
      let dateTime=this.bizFormModel.startDate
      this.bizFormModel.startDate=dateTime[0]
      this.bizFormModel.endDate=dateTime[1]
      this.bizFormModel.amount=this.bizFormModel.amount-0
      this.bizFormModel.number=this.bizFormModel.amount
      let amount=0
      this.memberData.forEach(item=>{
          amount+=  Math.ceil(BigNumber(item.number).multipliedBy(item.salePrice).toNumber())
          item.company=this.bizFormModel.company
      })
      this.bizFormModel.money=amount
   
      
      console.log(this.memberData,'撒开发立刻解放拉萨');
      this.bizFormModel.memberItem=this.memberData
       console.log(this.bizFormModel,'看这个值');
      this.setLoad()
      saveMemberSet(this.bizFormModel).then(responseData => {
        if(responseData.code == 100) {
          this.dialogProps.visible = false
          this.$emit('save-finished')
        } else {
          this.showMessage(responseData)
        }
        this.resetLoad()
      }).catch(error => {
        this.outputError(error)
      })
    },
    switchEdit() {
      this.dialogProps.action = 'edit'
      this.dialogProps.title = '修改会员卡'
      this.initOptions(this.bizFormModel)
       this.memberData=this.bizFormModel.memberItem
        this.memberData.forEach(item=>{
          item.itemType=item.costItem.itemType.name
          item.unit=item.costItem.unit.name
          item.salePrice=item.costItem.salePrice
        })
    },
    onDialogClose() {
      this.dialogProps.visible = false  
    },
    onDialogOpen() {
      this.$nextTick(() => {
        this.$refs['memberSetForm'].clearValidate()
      })
    },
    initFormModel(This) {
      return {
        'company': {     // 诊所id
          'id': currentUser.company.id,
          'name': null,
        },
        'name': '',   // 会员卡名称
        'type': {name:'体验卡',
                value:'memberType_0'},   // 会员卡类型
        'money': 0,    // 会员卡金额
        'amount': 0,    // 会员卡总数
        'number': 0,    // 剩余数量
        'status': '0',   // 是否启用（0：启用，1:禁用）
        'failure': '0',   // 是否失效（0：否，1：是）
        'startDate': '',   // 开始时间
        'endDate': '',   // 结束时间

      }
    },
    initOptions(This) {
      this.getType()
      this.getCostItem()
      this.memberData=[
    {
      name:'',//项目名称
      itemType:'',  //项目类型
      salePrice:0,   //项目金额
      unit:"" ,  //单位
      number:""  //赠送数量
      
    }]
      let company_search = {
        params: []
      }
        // 字段对应表上filter条件
        company_search.params.push.apply(company_search.params, [])
      // 数据权限: 诊所org_company
      this.pushDataPermissions(company_search.params, this.$route.meta.routerId, '41040096140492800')
      this.company_List.splice(0, this.company_List.length)
      listCompanyAll(company_search).then(responseData => {
        this.company_List = responseData.data
        // 获取初始项的值
        this.company_List.forEach( item => {
          if(item.id == this.bizFormModel.company.id) {
            this.bizFormModel.company = item
            return
          }
        })
      })

      //获取会员卡类型
    }
  },
  watch: {
  },
  mounted: function() {
    this.$nextTick(() => {
      this.$on('openViewMemberSetDialog', function(memberSet) {
        this.dialogProps.action = 'view'
        this.dialogProps.title = '查看会员卡'
        this.bizFormModel = {...this.initFormModel(), ...memberSet}
        this.initOptions(this.bizFormModel)
        this.bizFormModel.startDate=[this.bizFormModel.startDate,this.bizFormModel.endDate]
        this.memberData=this.bizFormModel.memberItem
        this.memberData.forEach(item=>{
          item.itemType=item.costItem.itemType.name
          item.unit=item.costItem.unit.name
          item.salePrice=item.costItem.salePrice
        })
        this.tabIndex = '1'
        this.dialogProps.visible = true
      })
      this.$on('openEditMemberSetDialog', function(memberSet) {
        this.dialogProps.action = 'edit'
        this.dialogProps.title = '修改会员卡'
        this.bizFormModel = {...this.initFormModel(), ...memberSet}
        this.initOptions(this.bizFormModel)
         this.bizFormModel.startDate=[this.bizFormModel.startDate,this.bizFormModel.endDate]
        this.memberData=this.bizFormModel.memberItem
        this.memberData.forEach(item=>{
          item.itemType=item.costItem.itemType.name
          item.unit=item.costItem.unit.name
          item.salePrice=item.costItem.salePrice
        })
        this.tabIndex = '1'
        this.dialogProps.visible = true
      })
      this.$on('openAddMemberSetDialog', function() {
        this.dialogProps.action = 'add'
        
        this.dialogProps.title = '添加会员卡'
        this.bizFormModel = this.initFormModel()
        this.initOptions(this.bizFormModel)
        this.tabIndex = '1'
        this.dialogProps.visible = true
      })
      this.$on('openCopyMemberSetDialog', function(memberSet) {
        this.dialogProps.action = 'add'
        this.dialogProps.title = '添加会员卡'
        this.bizFormModel = {...this.initFormModel(), ...memberSet}
        this.initOptions(this.bizFormModel)
        this.tabIndex = '1'
        this.bizFormModel.id = null   //把id设置为空，添加一个新的
        this.dialogProps.visible = true
      })
    })
  }  
}
</script>