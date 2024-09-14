<template>
  <el-row v-loading='loading'>
    <!-- 历史记录  -->
    <History :bussObject='curentRow' ></History>
    <!-- 编辑窗口  -->
    <memberSet-form ref='memberSetForm' :permission='permission' v-on:save-finished='getMemberSetList()'></memberSet-form>
    <div class="page-container">
        <!--  搜索栏  开始 -->
        <div class='query-form-container'>
          <el-row v-if='!moreCodition' class='search-row'>
            <el-form :model='queryModel' @submit.native.prevent label-position="left" label-width='100px' ref='queryForm' :inline-message='true'>
                      <el-col :span="5">
              <el-form-item label='会员卡名称' prop='name'>
                <el-input v-model='queryModel.name' :clearable='true' placeholder='请输入会员卡名称'></el-input>
              </el-form-item>
            </el-col>
              <el-col :span="4">
             <el-form-item label="类型" prop="memberType">
                <el-select
                  v-model="queryModel.memberType"
                  value-key="value"
                  filterable
                  clearable
                  placeholder="请选择类型"
                >
                  <el-option
                    v-for="item in memberType"
                    :key="item.value"
                    :label="item.name"
                    :value="item"
                  ></el-option>
                </el-select>
              </el-form-item>
              </el-col>
              <el-col :span="5">
             <el-form-item label="是否启用" prop="status">
                <el-select
                  v-model="queryModel.status"
                  value-key="value"
                  filterable
                  clearable
                  placeholder="请选择是否启用"
                >
                  <el-option
                    v-for="item in status"
                    :key="item.value"
                    :label="item.name"
                    :value="item.value"
                  ></el-option>
                </el-select>
              </el-form-item>
              </el-col>
              <el-col :span="5">
             <el-form-item label="是否有效" prop="valid">
                <el-select
                  v-model="queryModel.valid"
                  value-key="value"
                  filterable
                  clearable
                  placeholder="请选择是否有效"
                >
                  <el-option
                    v-for="item in valid"
                    :key="item.value"
                    :label="item.name"
                    :value="item.value"
                  ></el-option>
                </el-select>
              </el-form-item>
              </el-col>
              
             
              <!-- <el-col :span="6">
                <el-button-group>
                  <el-tooltip  effect="light" content="搜索" placement="top-start">
                    <el-button type="primary" icon="el-icon-search" @click='onSearch()' :plain='true'></el-button>
                  </el-tooltip>
                   <el-tooltip  effect="light" content="重置" placement="top-start">
                    <el-button type="primary" icon="el-icon-refresh-left" @click='$refs.queryForm.resetFields()' :plain='true'></el-button>
                  </el-tooltip>
                  <el-tooltip  effect="light" content="更多" placement="top-start">
                    <el-button type="primary" icon="el-icon-d-arrow-right" @click='onMoreCodition()' :plain='true'></el-button>
                  </el-tooltip>
                </el-button-group>
              </el-col> -->
               <el-col :span="2" style="text-align:right;padding-right:5px;float:right;">
                    <el-button-group>
                        <el-button
                          v-show="permission.add"
                          type="primary"
                          icon="el-icon-plus"                   
                          @click="onCreateMemberSet()"
                          >添加</el-button
                        >
                    </el-button-group>
                  </el-col>
                    <el-col :span="3" style="display:flex;justivy-content:space-around;float:right;">
               <el-button
                    type="primary"
                    icon="el-icon-search"
                    @click="onSearch()"
                    :plain="true"
                  >搜索</el-button>
                <el-button
                    type="info"
                    icon="el-icon-refresh-left"
                    @click="reset"
                    :plain="true"
                  >重置</el-button>
            </el-col>
            </el-form>
          </el-row>
          <QueryForm v-else v-model='moreParm' :tableId='tableId' :schemeId='schemeId'  :routerId='$route.meta.routerId' @search='onSearch()' @moreCodition='onMoreCodition()'></QueryForm>
        </div>
        <!--  搜索栏  结束 -->

        <!-- 工具栏 开始 -->
        <!-- <div class="page-container-header-end">
          <div>
            <el-button v-show='permission.add' type='primary' icon='el-icon-plus' :plain='true' @click='onCreateMemberSet()'>添加</el-button>
          </div>
        </div> -->
        <!-- 工具栏 结束 -->

      <!-- 表格栏  开始 -->
      <el-row>
        <el-col :span='24'>
          <div @mouseleave='moveTableOutside'>
            
            <el-table class='drag_table' ref="tableRef" height="calc(100vh - 240px)" :data='memberSetList' border @sort-change='onSortChange' @header-dragend='onChangeWidth' :cell-class-name='cellClassName' :header-cell-class-name='headerCellClassName' highlight-current-row>                
               <el-table-column
              label="序号"
              type="index"
              :index="indexMethod"
              align="center">
            </el-table-column>       
              <el-table-column v-for="(cv, index) in columnViews" v-if='cv.display' :prop='cv.prop' :key="`columnViews_${index}`" :label='cv.label' sortable='custom' :align='cv.align' :min-width='cv.miniWidth+"px"' :width='cv.width+"px"' header-align='center' :column-key='index.toString()' :render-header="renderHeader">
                <template slot-scope='{row,$index}'>
                  <span v-if='columnViews[index].showType == "Switch" || columnViews[index].showType == "Checkbox" || columnViews[index].showType == "Radio"'>
                    <li v-if='getAttrValue(row, columnViews[index].prop) == "0"' class='el-icon-check' style='color:#F56C6C;'></li>
                  </span>
                  <span v-else-if="columnViews[index].prop=='type'">
                    {{memberSetList[$index].type.name}}
                  </span>
                  <span v-else>{{ getAttrValue(row, columnViews[index].prop, columnViews[index].javaType )}}</span>
                </template>
              </el-table-column>
              <!--表行级操作按钮-->
              <el-table-column label='操作' header-align='center' :width='oprColumnWidth + "px"' fixed='right' :key="Math.random()">        
                <template slot='header' slot-scope="scope">
                  <span>操作</span>
                  <view-columns-select v-model='columnViews' v-on:save-column-view='saveColumn' v-on:show-all-column='showAllColumn' v-on:show-default-column='showDefaultColumn'></view-columns-select>
                  <export-excel-button v-show='permission.export' :data='memberSetList' :tHeader='getHeads()' :filterVal='getFilterVal()' :plain='true'></export-excel-button>
                </template>
                <template slot-scope='scope'>
                  <!-- <OperationIcon v-show='permission.view' type='info' content='查看' placement='top-start' icon-name='el-icon-view' 
                    @click='onViewMemberSet(scope.$index, scope.row)'></OperationIcon> -->
                   <center> 
                    <el-button type="text" v-show='permission.view' @click='onViewMemberSet(scope.$index, scope.row)'>详情</el-button>
                    <el-button v-if="scope.row.amount==scope.row.number" style="color:red" type="text" v-show='permission.view' @click='onDeleteMemberSet(scope.$index, scope.row)'>删除</el-button>
                  </center>
                    
                  <!-- <OperationIcon v-show='permission.edit' type='primary' content='编辑' placement='top-start' icon-name='el-icon-edit' 
                    @click='onEditMemberSet(scope.$index, scope.row)'></OperationIcon>
                  <OperationIcon v-show='permission.add' type='primary' content='复制' placement='top-start' icon-name='el-icon-document' 
                    @click='onCopyMemberSet(scope.$index, scope.row)'></OperationIcon>
                  <OperationIcon v-show='permission.remove' type='danger' content='删除' placement='top-start' icon-name='el-icon-delete' 
                    @click='onDeleteMemberSet(scope.$index, scope.row)'></OperationIcon>
                  <OperationIcon v-show='permission.view' type='info' content='历史记录' placement='top-start' icon-name='el-icon-info' 
                    @click='onShowHistory(scope.$index, scope.row)'></OperationIcon> -->
                </template>
              </el-table-column>
            </el-table>
    	  </div>	          
        </el-col>
      </el-row>
      <!-- 表格栏  结束 -->
      <!-- 分页栏     开始 -->
      <el-row>
        <el-col :span='24'>
          <el-pagination
            background     
            @size-change='onSizeChange'
            @current-change='onCurrentChange'
            :current-page.sync='currentPage'
            :page-sizes='[20, 50, 100, memberSetTotal]'
            :page-size='20'
            layout='total, sizes, prev, pager, next, jumper'
            :total='memberSetTotal'>
          </el-pagination>
        </el-col>
      </el-row>
      <!-- 分页栏     结束 -->     </div>
  </el-row>
</template>

<script>
import { validatenull } from '@/utils/validate'
import { listMemberSetPage, getMemberSetById, deleteMemberSet } from '@/api/member/memberSet'
import { listResourcePermission } from '@/api/admin/common/permission'
import MemberSetForm from './memberSetForm'
import ExportExcelButton from '@/components/ExportExcelButton'
import ViewColumnsSelect from '@/views/components/ViewColumnsSelect'
import QueryForm from '@/views/components/queryForm'
import MainUI from '@/views/components/mainUI'
import OperationIcon from '@/components/OperationIcon'
import History from '@/views/components/history'
import { listDictItemAll } from '@/api/sys/dictItem'
export default {
  extends: MainUI,
  components: { 
    MemberSetForm,
    ExportExcelButton,
    ViewColumnsSelect,
    QueryForm,
    OperationIcon,
    History
  },
  data() {
    return {
      permission: {
        view: false,
        add: false,
        edit: false,
        remove: false,
        export: false
      },
      queryTypes: {
        'name': 'like',
      },
      queryModel: {
        'name': '',   // 会员卡名称
        status:'',
        valid:'',
        memberType:{}
      },
      search: {
        params: [{columnName: 'company_id', queryType: '=', value: currentUser.company.id}],    
        offset: 0,
        limit: 20,
		columnName: '',      // 排序字段名
        order: ''            // 排序
      },
      currentPage: 1,
      memberSetTotal: 0,
      memberSetList: [],
        
      
      oprColumnWidth: 140,  // 操作列宽
      tableId: '1222698883343517159',
      schemeId: '1222698883343517177',
      status:[{
        name:'是',
        value:"0",
      },{
        name:'否',
        value:'1',
      }],
      valid:[
        {
          name:'是',
          value:'0'
        },
        {
          name:'否',
          value:'1'
        }
      ],
      memberType:[],//会员卡类型
    }
  },
  methods: {
     indexMethod(index){
       return (this.currentPage-1)*this.search.limit+index +1;
    },
    //重置
     reset(){
      this.queryModel={
         'name': '',   // 会员卡名称
        status:'',
        valid:'',
        memberType:{}
      }
      this.getMemberSetList()
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
    getMemberSetList() {
      this.setLoad()
      /* 查询参数 和数据权限 */
      this.search.params = [{columnName: 'company_id', queryType: '=', value: currentUser.company.id}]
      if(this.moreCodition) {
        this.search.params = this.search.params.concat(this.compositeCondition())
      }else{
        // 查询参数: 会员卡名称
        this.search.params.push({
      	  columnName: 'type',
      	  queryType: '=',
          value: this.queryModel.memberType.value
        })
        this.search.params.push({
          columnName: 'name',
      	  queryType: 'like',
          value: this.queryModel.name
        })
         this.search.params.push({
      	  columnName: 'status',
      	  queryType: '=',
          value: this.queryModel.status
        })
         this.search.params.push({
      	  columnName: 'failure',
      	  queryType: '=',
          value: this.queryModel.valid
        })
      }
      // 数据权限: 会员卡设置member_set
      this.pushDataPermissions(this.search.params, this.$route.meta.routerId, this.tableId)
      listMemberSetPage(this.search).then(responseData => {
        if(responseData.code == 100) {
          this.memberSetTotal = responseData.data.total
          this.memberSetList = responseData.data.rows
          console.log(this.memberSetList,'查看数值');
        } else {
          this.showMessage(responseData)
        }
        this.resetLoad()
      }).catch(error => {
        this.outputError(error)
      })
    },
    onSearch() {
      if(this.moreCodition) {
        this.search.offset = 0
        this.currentPage = 1
        this.getMemberSetList()
      } else {
        this.$refs['queryForm'].validate(valid => {
          if (valid) {
            this.search.offset = 0
            this.currentPage = 1
            this.getMemberSetList()
          } else {
            return false
          }
        })
      }
    },
    onSizeChange(val) {
      this.currentPage = 1
      this.search.limit = val;
      this.search.offset = (this.currentPage - 1) * val
      this.getMemberSetList()
    },
    onCurrentChange(val) {
      this.search.offset = (val - 1) * this.search.limit
      this.currentPage = val
      this.getMemberSetList()
    },
    async pageInit() {
      
      this.setLoad()
      try {
        this.getType()
        this.initOptions(this.queryModel)
        this.search.params = [{columnName: 'company_id', queryType: '=', value: currentUser.company.id}]
        // 数据权限: 会员卡设置member_set
        this.pushDataPermissions(this.search.params, this.$route.meta.routerId, this.tableId)
        let [listMemberSetRespData, listPermissionRespData] = await Promise.all([
          listMemberSetPage(this.search),
          listResourcePermission(this.$route.meta.routerId)
        ])
        if(listMemberSetRespData.code == 100 && listPermissionRespData.code == 100) {
          this.memberSetTotal = listMemberSetRespData.data.total
          this.memberSetList = listMemberSetRespData.data.rows
           console.log(this.memberSetList,'查看数值');
          this.permission.view = listPermissionRespData.data.find(item => {
            return item.permission === 'memberSet:read'
          })
          this.permission.export = listPermissionRespData.data.find(item => {
            return item.permission === 'memberSet:export'
          })
          this.permission.add = listPermissionRespData.data.find(item => {
            return item.permission === 'memberSet:create'
          })
          this.permission.edit = listPermissionRespData.data.find(item => {
            return item.permission === 'memberSet:update'
          })
          this.permission.remove = listPermissionRespData.data.find(item => {
            return item.permission === 'memberSet:delete'
          })
        } else {
          this.showMessage(listPermissionRespData.code != 100 ? listPermissionRespData : listMemberSetRespData)
        }
        this.resetLoad()
      } catch(error) {
        this.outputError(error) 
      }
    },
    onViewMemberSet(index, row) {
      this.setLoad()
      getMemberSetById(row.id).then(responseData => {
        if(responseData.code == 100) {
          this.$refs.memberSetForm.$emit('openViewMemberSetDialog', responseData.data)
        } else {
          this.showMessage(responseData)
        }
        this.resetLoad()
      }).catch(error => {
        this.outputError(error)
      })
    },
    onCreateMemberSet() {
      this.$refs.memberSetForm.$emit('openAddMemberSetDialog')
    },
    onEditMemberSet(index, row) {
      this.setLoad()
      getMemberSetById(row.id).then(responseData => {
        if(responseData.code == 100) {
          this.$refs.memberSetForm.$emit('openEditMemberSetDialog', responseData.data)
        }else{
          this.showMessage(responseData)
        }
        this.resetLoad()
      }).catch(error => {
        this.outputError(error)
      })
    },
    onCopyMemberSet(index, row) {
      this.setLoad()
      getMemberSetById(row.id).then(responseData => {
        if(responseData.code == 100) {
          this.$refs.memberSetForm.$emit('openCopyMemberSetDialog', responseData.data)
        } else {
          this.showMessage(responseData)
        }
        this.resetLoad()
      }).catch(error => {
        this.outputError(error)
      })
    },
    onDeleteMemberSet(index, row) {
      this.$confirm('确定删除吗？', '确认', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.setLoad()
        deleteMemberSet(row).then(responseData => {
          if(responseData.code == 100) {
            this.getMemberSetList()
            this.showMessage({type: 'success', msg: '删除成功'})
          } else {
            this.showMessage(responseData)
          }
          this.resetLoad()
        }).catch(error => {
          this.outputError(error)  
        })
      }).catch(() => {})
    },
    onSortChange( orderby ) {
      if(validatenull(orderby.prop)) {
        this.search.columnName = ''
        this.search.order = ''
      } else  {
        this.search.columnName = orderby.prop
        this.search.order = orderby.order === 'descending' ? 'desc' : 'asc'
      }

      this.getMemberSetList()
    },
    initOptions(This) {
    } 
  },
  watch: {
  },
  updated(){
     if(this.$refs.tableRef){
       this.$nextTick(() => {
            // tableRef是el-table绑定的ref属性值
            this.$refs.tableRef.doLayout()// 对 Table 进行重新布局
        })
    }
  },
  mounted() {
    this.pageInit()
  }
}
</script>
<style lang="scss" scoped>
.drag_table {
 // 设置表格header的高度
 /deep/ th {
   height: 44px;
 }
/deep/ th.gutter:last-of-type {
  height: 0 !important;
}
 // 设置表格body的高度
 /deep/.el-table__body-wrapper {
  //解决数据展示超出body高度不滚动bug
  overflow-y: auto;
   // 减去的是表格header的高度
   height: calc(100% - 44px) !important;
 }

 .el-table__fixed-right {
      height: 100% !important;
  }
}
</style>