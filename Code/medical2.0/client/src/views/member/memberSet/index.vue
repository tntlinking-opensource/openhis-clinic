<template>
  <el-row v-loading='loading'>
    <!-- 历史记录  -->
    <History :bussObject='curentRow' ></History>
    <!-- 编辑窗口  -->
    <memberSet-form ref='memberSetForm' :permission='permission' @save-finished='loadData'></memberSet-form>
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
                  <span v-if='columnViews[index].showType === "Switch" || columnViews[index].showType === "Checkbox" || columnViews[index].showType === "Radio"'>
                    <li v-if='getAttrValue(row, columnViews[index].prop) === "0"' class='el-icon-check' style='color:#F56C6C;'></li>
                  </span>
                  <span v-else-if="columnViews[index].prop==='type'">
                    {{memberSetList[$index].type.name}}
                  </span>
                  <span v-else>{{ getAttrValue(row, columnViews[index].prop, columnViews[index].javaType )}}</span>
                </template>
              </el-table-column>
              <!--表行级操作按钮-->
              <el-table-column label='操作' header-align='center' :width='oprColumnWidth + "px"' :key="'operate'">        
                <template slot='header' slot-scope="scope">
                  <span>操作</span>
                  <view-columns-select v-model='columnViews' v-on:save-column-view='saveColumn' v-on:show-all-column='showAllColumn' v-on:show-default-column='showDefaultColumn'></view-columns-select>
                  <export-excel-button v-show='permission.export' :data='memberSetList' :tHeader='getHeads()' :filterVal='getFilterVal()' :plain='true'></export-excel-button>
                </template>
                <template slot-scope='scope'>
                  <!-- <OperationIcon v-show='permission.view' type='info' content='查看' placement='top-start' icon-name='el-icon-view' 
                    @click='onViewMemberSet(scope.$index, scope.row)'></OperationIcon> -->
                   <center> 
                    <el-button type="text" v-show='permission.view' @click='onViewEntity(scope.$index, scope.row, "memberSetForm")'>详情</el-button>
                    <el-button v-if="scope.row.amount===scope.row.number" style="color:red" type="text" v-show='permission.view' @click='onDeleteEntity(scope.$index, scope.row, deleteApi)'>删除</el-button>
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
import listViewMixin from '@/mixins/listViewMixin'
import MemberSetForm from './memberSetForm'
import ExportExcelButton from '@/components/ExportExcelButton'
import ViewColumnsSelect from '@/views/components/ViewColumnsSelect'
import QueryForm from '@/views/components/queryForm'
import MainUI from '@/views/components/mainUI'
import OperationIcon from '@/components/OperationIcon'
import History from '@/views/components/history'
import { getDictItemsByCode, DICT_CODE } from '@/utils/dictCache'
export default {
  extends: MainUI,
  mixins: [listViewMixin],
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
      listApi: listMemberSetPage,
      getApi: getMemberSetById,
      deleteApi: deleteMemberSet,
      entityName: 'MemberSet',
      permissionPrefix: 'memberSet',
      queryTypes: {
        'name': 'like',
      },
      queryModel: {
        'name': '',   // 会员卡名称
        status:'',
        valid:'',
        memberType:{}
      },
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
    //重置
     reset(){
      this.queryModel={
         'name': '',   // 会员卡名称
        status:'',
        valid:'',
        memberType:{}
      }
      this.onSearch()
    },

     //获取会员卡类型
    initOptions(){
      getDictItemsByCode(DICT_CODE.MEMBER_TYPE).then((data) => {
        this.memberType = data;
        this.$forceUpdate()
      })
    },
    appendSearchParams() {
      this.search.params.push({
        columnName: 'company_id',
        queryType: '=',
        value: currentUser.company.id
      })
      if(this.moreCodition) {
        this.search.params = this.search.params.concat(this.compositeCondition())
      }else{
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
      this.pushDataPermissions(this.search.params, this.$route.meta.routerId, this.tableId)
    },
    handleListResponse(responseData) {
      this.memberSetTotal = responseData.data.total
      this.memberSetList = responseData.data.rows
    },
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
