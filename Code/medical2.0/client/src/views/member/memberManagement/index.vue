<template>
  <el-row v-loading='loading'>
    <!-- 历史记录  -->
    <History :bussObject='curentRow' ></History>
    <!-- 编辑窗口  -->
    <memberManagement-form ref='memberManagementForm' :permission='permission' v-on:save-finished='getMemberManagementList()'></memberManagement-form>
    <div class="page-container">
        <!--  搜索栏  开始 -->
        <div class='query-form-container'>
          <el-row v-if='!moreCodition' class='search-row'>
            <el-form :model='queryModel' @submit.native.prevent label-position="left" label-width='70px' ref='queryForm' :inline-message='true'>
                      <el-col :span="6">
              <el-form-item label='姓名' prop='name'>
                <el-input v-model='queryModel.name' :clearable='true' placeholder='请输入姓名'></el-input>
              </el-form-item>
            </el-col>
              <el-col :span="6">
              <el-form-item label='会员卡号' prop='card'>
                <el-input v-model='queryModel.card' :clearable='true' placeholder='请输入卡号'></el-input>
              </el-form-item>
            </el-col>
             <el-col :span="6">
             <el-form-item label="状态" prop="status">
                <el-select
                  v-model="queryModel.status"
                  value-key="value"
                  filterable
                  clearable
                  placeholder="请选择状态"
                >
                  <el-option
                    v-for="item in memberStatus"
                    :key="item.value"
                    :label="item.name"
                    :value="item.value"
                  ></el-option>
                </el-select>
              </el-form-item>
              </el-col>
               <el-col :span="6">
                      <el-form-item label='开卡日期' prop='updateDate'>
                       <el-date-picker
                        v-model="queryModel.updateDate"
                        type="datetimerange"
                        align="right"
                        start-placeholder="开始日期"
                        end-placeholder="结束日期"
                        value-format="yyyy-MM-dd HH:mm:ss"
                        format="yyyy-MM-dd HH:mm:ss"
                        :default-time="['00:00:00', '23:59:59']">
                      </el-date-picker>
                      </el-form-item>
                </el-col>
                 <el-col :span='6'>
                  <el-form-item label='会员卡类型' prop='type' label-width='100px'>
                    <el-select 
                    v-model="queryModel.type"
                    style="margin-left:-5px"
                    value-key="value"
                    filterable
                    clearable
                    placeholder="请选择会员卡类型"
                    @change="getMember">
                    <el-option v-for='item in typeList' :key='item.value' :label='item.name' :value='item.value'></el-option>
                    </el-select>
                  </el-form-item>
                </el-col>
                
                <el-col :span='6'>
                  <el-form-item label='会员卡名称' prop='memberName' label-width='100px'>
                
                    <el-select  
                    v-model="queryModel.memberName"
                    style="margin-left:-5px"
                    value-key="value"
                    filterable
                    clearable
                    placeholder="请选择会员卡名称"
                   >
                    <el-option  v-for='item in memberSet_List' :key='item.id' :label='item.name' :value='item.name'></el-option>
                    </el-select>
                  </el-form-item>
                </el-col>      
               <el-col :span="3" style="text-align:right;padding-right:5px;float:right;">
                    <el-button-group>
                        <el-button
                          v-show="permission.add"
                          type="primary"
                          icon="el-icon-plus"                   
                          @click="onCreateMemberManagement()"
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
            <el-button v-show='permission.add' type='primary' icon='el-icon-plus' :plain='true' @click='onCreateMemberManagement()'>添加</el-button>
          </div>
        </div> -->
        <!-- 工具栏 结束 -->

      <!-- 表格栏  开始 -->
      <el-row>
        <el-col :span='24'>
          <div @mouseleave='moveTableOutside'>        
            <el-table class='drag_table' ref="tableRef" height="calc(100vh - 240px)" :data='memberManagementList' border @sort-change='onSortChange' @header-dragend='onChangeWidth' :cell-class-name='cellClassName' :header-cell-class-name='headerCellClassName' highlight-current-row>                
               <el-table-column
              label="序号"
              type="index"
              :index="indexMethod"
              align="center">
            </el-table-column>
              <el-table-column v-for="(cv, index) in columnViews" v-if='cv.display' :prop='cv.prop' :key="`columnViews_${index}`" :label='cv.label' sortable='custom' :align='cv.align' :min-width='cv.miniWidth+"px"' :width='cv.width+"px"' header-align='center' :column-key='index.toString()' :render-header="renderHeader">
                <template slot-scope='{row,$index}'>
                  <span v-if='columnViews[index].showType == "Checkbox" || columnViews[index].showType == "Radio"'>
                    <li v-if='getAttrValue(row, columnViews[index].prop) == "1"' class='el-icon-check' style='color:#F56C6C;'></li>
                  </span>
                  <span v-else-if="columnViews[index].prop=='gender'">
                    <span v-if="memberManagementList[$index].gender=='gender_0'">男</span>
                    <span v-else>女</span>
                  </span>
                  <span v-else-if="columnViews[index].prop=='status'">
                    <span v-if="memberManagementList[$index].status=='0'">未使用</span>
                    <span v-else-if="memberManagementList[$index].status=='1'">已使用</span>
                    <span v-else-if="memberManagementList[$index].status=='2'">已禁用</span>
                    <span v-else>已失效</span>
                  </span>
                  <span v-else-if="columnViews[index].prop=='age'">
                    {{memberManagementList[$index].age}}岁
                  </span>
                   <span v-else-if="columnViews[index].prop=='type'">
                    <!-- <span v-if="memberManagementList[$index].type=='gender_0'">男</span> -->
                    {{memberManagementList[$index].type.name}}
                  </span>
                  <span v-else>{{ getAttrValue(row, columnViews[index].prop, columnViews[index].javaType )}}</span>
                </template>
              </el-table-column>
              <!--表行级操作按钮-->
              <el-table-column label='操作' header-align='center' :width='oprColumnWidth + "px"' fixed='right'>        
                <template slot='header' slot-scope="scope">
                  <span>操作</span>
                  <view-columns-select v-model='columnViews' v-on:save-column-view='saveColumn' v-on:show-all-column='showAllColumn' v-on:show-default-column='showDefaultColumn'></view-columns-select>
                  <export-excel-button v-show='permission.export' :data='memberManagementList' :tHeader='getHeads()' :filterVal='getFilterVal()' :plain='true'></export-excel-button>
                </template>
                <template slot-scope='scope'>
                    <center> 
                      <el-button type="text" v-show='permission.view' @click='onViewMemberManagement(scope.$index, scope.row)'>详情</el-button>
                      <el-button v-if="scope.row.status=='0'" type="text" v-show='permission.view' @click='disableMember(scope.$index, scope.row)' style="color:red;">禁用</el-button>
                    </center>
                  <!-- <OperationIcon v-show='permission.view' type='info' content='查看' placement='top-start' icon-name='el-icon-view' 
                    @click='onViewMemberManagement(scope.$index, scope.row)'></OperationIcon>
                  <OperationIcon v-show='permission.edit' type='primary' content='编辑' placement='top-start' icon-name='el-icon-edit' 
                    @click='onEditMemberManagement(scope.$index, scope.row)'></OperationIcon>
                  <OperationIcon v-show='permission.add' type='primary' content='复制' placement='top-start' icon-name='el-icon-document' 
                    @click='onCopyMemberManagement(scope.$index, scope.row)'></OperationIcon>
                  <OperationIcon v-show='permission.remove' type='danger' content='删除' placement='top-start' icon-name='el-icon-delete' 
                    @click='onDeleteMemberManagement(scope.$index, scope.row)'></OperationIcon>
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
            :page-sizes='[20, 50, 100, memberManagementTotal]'
            :page-size='20'
            layout='total, sizes, prev, pager, next, jumper'
            :total='memberManagementTotal'>
          </el-pagination>
        </el-col>
      </el-row>
      <!-- 分页栏     结束 -->     </div>
  </el-row>
</template>

<script>
import { validatenull } from '@/utils/validate'
import { listMemberManagementPage, getMemberManagementById, deleteMemberManagement} from '@/api/member/memberManagement'
import { listResourcePermission } from '@/api/admin/common/permission'
import MemberManagementForm from './memberManagementForm'
import ExportExcelButton from '@/components/ExportExcelButton'
import ViewColumnsSelect from '@/views/components/ViewColumnsSelect'
import QueryForm from '@/views/components/queryForm'
import MainUI from '@/views/components/mainUI'
import OperationIcon from '@/components/OperationIcon'
import History from '@/views/components/history'
import { listDictItemAll } from '@/api/sys/dictItem'
import { listMemberSetAll } from '@/api/member/memberSet'
export default {
  extends: MainUI,
  components: { 
    MemberManagementForm,
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
         name: '',   // 姓名
         card:'',   //卡号
         updateDate:[],  //时间
         status:"",  //状态
         type:"",   //类型
         memberName:'', //名称
      },
      search: {
        params: [{columnName: 'company_id', queryType: '=', value: currentUser.company.id}],    
        offset: 0,
        limit: 20,
		columnName: '',      // 排序字段名
        order: ''            // 排序
      },
      currentPage: 1,
      memberManagementTotal: 0,
      memberManagementList: [],
        
      
      oprColumnWidth: 140,  // 操作列宽
      tableId: '1225301049409470622',
      schemeId: '1225301049409470640',
      memberStatus:[
        {
          name:"未使用",
          value:'0'
        },
        {
          name:"已使用",
          value:'1'
        },
        {
          name:"已禁用",
          value:'2'
        },
        {
          name:"已失效",
          value:'4'
        },
      ],
      typeList:[],  //会员卡类型
      memberSet_List:[], // 会员卡名称
    }
  },
  methods: {
     indexMethod(index){
       return (this.currentPage-1)*this.search.limit+index +1;
    },
    //搜索重置
    reset(){
      this.queryModel={
         name: '',   // 姓名
         card:'',   //卡号
         updateDate:[],  //时间
         status:"",  //状态
         type:"",   //类型
         memberName:'', //名称
      }
      this.memberSet_List=[]
      this.getMemberManagementList()
    },
    //获取对应会员卡类型的会员卡
    getMember(row){
      
      let memberSet_search = {
        params: []
      }
        // 字段对应表上filter条件
        memberSet_search.params.push.apply(memberSet_search.params, [{columnName: 'company_id', queryType: '=', value: currentUser.company.id},{columnName: 'type', queryType: '=', value: row.value},{columnName: 'status', queryType: '=', value: 0},{columnName: 'failure', queryType: '=', value: 0},{columnName: 'number', queryType: '>', value: 0}])
      // 数据权限: 会员卡设置member_set
      this.pushDataPermissions(memberSet_search.params, this.$route.meta.routerId, '1222698883343517159')
      this.memberSet_List.splice(0, this.memberSet_List.length)
      listMemberSetAll(memberSet_search).then(responseData => {
        this.memberSet_List = responseData.data
      })
    },
    //禁用会员
    disableMember(index,row){
      deleteMemberManagement(row).then((res)=>{
        if(res.code=='100'){
            this.getMemberManagementList()
            this.showMessage({type: 'success', msg: '禁用成功'})
        }else{
           this.showMessage(res)
        }
      }).catch((error)=>{
         this.outputError(error)  
      })
    },
    getMemberManagementList() {
      this.setLoad()
      /* 查询参数 和数据权限 */
      this.search.params = [{columnName: 'company_id', queryType: '=', value: currentUser.company.id}]
      if(this.moreCodition) {
        this.search.params = this.search.params.concat(this.compositeCondition())
      }else{
        // 查询参数: 姓名
        this.search.params.push({
      	  columnName: 'name',
      	  queryType: 'like',
          value: this.queryModel.name
        })
         if(this.queryModel.card!=''){
           this.search.params.push({
      	  columnName: 'card',
      	  queryType: '=',
          value: this.queryModel.card
        })
         }
         if(this.queryModel.status!=''){
           this.search.params.push({
      	  columnName: 'status',
      	  queryType: '=',
          value: this.queryModel.status
        })
         }
         if(this.queryModel.type!=''){
           this.search.params.push({
      	  columnName: 'type',
      	  queryType: '=',
          value: this.queryModel.type
        })
         }
         if(this.queryModel.memberName!=''){
           this.search.params.push({
      	  columnName: 'member_name',
      	  queryType: 'like',
          value: this.queryModel.memberName
        })
         }
         if (this.queryModel.updateDate && this.queryModel.updateDate.length) {
                  this.search.params.push({
                      logic: "AND",
                      queryType: "("
                    },{
                      columnName: "update_date",
                      logic: "",
                      queryType: 'between',
                      value: this.queryModel.updateDate,
                    },{
                      logic: "",
                      queryType: ")"
                    })
                }
      }
      // 数据权限: 会员卡管理member_management
      this.pushDataPermissions(this.search.params, this.$route.meta.routerId, this.tableId)
      listMemberManagementPage(this.search).then(responseData => {
        if(responseData.code == 100) {
          this.memberManagementTotal = responseData.data.total
          this.memberManagementList = responseData.data.rows
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
        this.getMemberManagementList()
      } else {
        this.$refs['queryForm'].validate(valid => {
          if (valid) {
            this.search.offset = 0
            this.currentPage = 1
            this.getMemberManagementList()
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
      this.getMemberManagementList()
    },
    onCurrentChange(val) {
      this.search.offset = (val - 1) * this.search.limit
      this.currentPage = val
      this.getMemberManagementList()
    },
    async pageInit() {
      this.setLoad()
      try {
        this.initOptions(this.queryModel)
        this.search.params = [{columnName: 'company_id', queryType: '=', value: currentUser.company.id}]
        // 数据权限: 会员卡管理member_management
        this.pushDataPermissions(this.search.params, this.$route.meta.routerId, this.tableId)
        let [listMemberManagementRespData, listPermissionRespData] = await Promise.all([
          listMemberManagementPage(this.search),
          listResourcePermission(this.$route.meta.routerId)
        ])
        if(listMemberManagementRespData.code == 100 && listPermissionRespData.code == 100) {
          this.memberManagementTotal = listMemberManagementRespData.data.total
          this.memberManagementList = listMemberManagementRespData.data.rows
          console.log(this.memberManagementList,'看情况');
          this.permission.view = listPermissionRespData.data.find(item => {
            return item.permission === 'memberManagement:read'
          })
          this.permission.export = listPermissionRespData.data.find(item => {
            return item.permission === 'memberManagement:export'
          })
          this.permission.add = listPermissionRespData.data.find(item => {
            return item.permission === 'memberManagement:create'
          })
          this.permission.edit = listPermissionRespData.data.find(item => {
            return item.permission === 'memberManagement:update'
          })
          this.permission.remove = listPermissionRespData.data.find(item => {
            return item.permission === 'memberManagement:delete'
          })
        } else {
          this.showMessage(listPermissionRespData.code != 100 ? listPermissionRespData : listMemberManagementRespData)
        }
        this.resetLoad()
      } catch(error) {
        this.outputError(error) 
      }
      //获取会员卡类型
      let type_search={
        params:[
          {
            
            columnName: "dict_type_id",
            queryType: "=",
            value: '1224037951067242497',
          
          }
        ]
      }
     listDictItemAll(type_search).then((responseData) => {
          if(responseData.code=='100'){
            
            this.typeList=responseData.data
            this.$forceUpdate()
          }
      })
    },
    onViewMemberManagement(index, row) {
      this.setLoad()
      getMemberManagementById(row.id).then(responseData => {
        if(responseData.code == 100) {

          this.$refs.memberManagementForm.$emit('openViewMemberManagementDialog', responseData.data)
        } else {
          this.showMessage(responseData)
        }
        this.resetLoad()
      }).catch(error => {
        this.outputError(error)
      })
    },
    onCreateMemberManagement() {
      this.$refs.memberManagementForm.$emit('openAddMemberManagementDialog')
    },
    onEditMemberManagement(index, row) {
      this.setLoad()
      getMemberManagementById(row.id).then(responseData => {
        if(responseData.code == 100) {
          this.$refs.memberManagementForm.$emit('openEditMemberManagementDialog', responseData.data)
        }else{
          this.showMessage(responseData)
        }
        this.resetLoad()
      }).catch(error => {
        this.outputError(error)
      })
    },
    onCopyMemberManagement(index, row) {
      this.setLoad()
      getMemberManagementById(row.id).then(responseData => {
        if(responseData.code == 100) {
          this.$refs.memberManagementForm.$emit('openCopyMemberManagementDialog', responseData.data)
        } else {
          this.showMessage(responseData)
        }
        this.resetLoad()
      }).catch(error => {
        this.outputError(error)
      })
    },
    onDeleteMemberManagement(index, row) {
      this.$confirm('确定删除吗？', '确认', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.setLoad()
        deleteMemberManagement(row).then(responseData => {
          if(responseData.code == 100) {
            this.getMemberManagementList()
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

      this.getMemberManagementList()
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