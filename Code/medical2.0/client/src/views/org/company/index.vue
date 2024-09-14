<template>
  <el-row v-loading='loading'>
    <!-- 历史记录  -->
    <History :bussObject='curentRow' ></History>
    <!-- 编辑窗口  -->
    <company-form ref='companyForm' :permission='permission' v-on:save-finished='getCompanyList()'></company-form>
    <div class="page-container">
        <!--  搜索栏  开始 -->
        <div class='query-form-container'>
          <el-row v-if='!moreCodition' class='search-row'>
            <el-form :model='queryModel' @submit.native.prevent label-position="left" label-width='70px' ref='queryForm' :inline-message='true'>
                      <el-col :span="6">
              <el-form-item label='名称' prop='name'>
                <el-input v-model='queryModel.name' :clearable='true' placeholder='请输入名称'></el-input>
              </el-form-item>
            </el-col>
              <el-col :span="6">
              <el-form-item label='电话' prop='phoneNumber'>
                <el-input v-model='queryModel.phoneNumber' :clearable='true' placeholder='请输入电话'></el-input>
              </el-form-item>
            </el-col>
              <el-col :span="6">
              <el-form-item label='诊所版本' prop='version'>
                <el-select v-model='queryModel.version' value-key='id'  filterable clearable placeholder='请选择诊所版本'>
                   <el-option v-for='version in version_List' :key='version.id' :label='version.name' :value='version'></el-option>
                </el-select>
              </el-form-item>
            </el-col>
              <el-col :span="6">
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
              </el-col>
            </el-form>
          </el-row>
          <QueryForm v-else v-model='moreParm' :tableId='tableId' :schemeId='schemeId'  :routerId='$route.meta.routerId' @search='onSearch()' @moreCodition='onMoreCodition()'></QueryForm>
        </div>
        <!--  搜索栏  结束 -->

        <!-- 工具栏 开始 -->
        <div class="page-container-header-end">
          <div>
            <el-button v-show='permission.add' type='primary' icon='el-icon-plus'  @click='onCreateCompany()'>添加</el-button>
          </div>
        </div>
        <!-- 工具栏 结束 -->

      <!-- 表格栏  开始 -->
      <el-row>
        <el-col :span='24'>
          <div @mouseleave='moveTableOutside'>        
            <el-table class='drag_table' :data='companyList' row-key='id' :tree-props="{children: 'children', hasChildren: 'hasChildren'}"border @sort-change='onSortChange' @header-dragend='onChangeWidth' :cell-class-name='cellClassName' :header-cell-class-name='headerCellClassName' highlight-current-row>                
              <el-table-column v-for="(cv, index) in columnViews" v-if='cv.display' :prop='cv.prop' :key="`columnViews_${index}`" :label='cv.label' sortable='custom' :align='cv.align' :min-width='cv.miniWidth+"px"' :width='cv.width+"px"' header-align='center' :column-key='index.toString()' :render-header="renderHeader">
                <template slot-scope='{row,$index}'>
                  <span v-if='columnViews[index].showType == "Switch" || columnViews[index].showType == "Checkbox" || columnViews[index].showType == "Radio"'>
                    <li v-if='getAttrValue(row, columnViews[index].prop) == "1"' class='el-icon-check' style='color:#F56C6C;'></li>
                  </span>
                  <span v-else>{{ getAttrValue(row, columnViews[index].prop, columnViews[index].javaType )}}</span>
                </template>
              </el-table-column>
              <!--表行级操作按钮-->
              <el-table-column label='操作' header-align='center' :width='oprColumnWidth + "px"' fixed='right'>        
                <template slot='header' slot-scope="scope">
                  <span>操作</span>
                  <view-columns-select v-model='columnViews' v-on:save-column-view='saveColumn' v-on:show-all-column='showAllColumn' v-on:show-default-column='showDefaultColumn'></view-columns-select>
                  <export-excel-button v-show='permission.export' :data='companyList' :tHeader='getHeads()' :filterVal='getFilterVal()' :plain='true'></export-excel-button>
                </template>
                <template slot-scope='scope'>
                  <OperationIcon v-show='permission.view' type='info' content='查看' placement='top-start' icon-name='el-icon-view' 
                    @click='onViewCompany(scope.$index, scope.row)'></OperationIcon>
                  <OperationIcon v-show='permission.add' type='primary' content='添加下级' placement='top-start' icon-name='el-icon-circle-plus-outline' 
                    @click='onCreateCompany(scope.$index, scope.row)'></OperationIcon>
                  <OperationIcon v-show='permission.edit' type='primary' content='编辑' placement='top-start' icon-name='el-icon-edit' 
                    @click='onEditCompany(scope.$index, scope.row)'></OperationIcon>
                  <OperationIcon v-show='permission.add' type='primary' content='复制' placement='top-start' icon-name='el-icon-document' 
                    @click='onCopyCompany(scope.$index, scope.row)'></OperationIcon>
                  <OperationIcon v-show='permission.remove && (!(scope.row.children) || scope.row.children.length <=0)' type='danger' content='删除' placement='top-start' icon-name='el-icon-delete' 
                    @click='onDeleteCompany(scope.$index, scope.row)'></OperationIcon>
                  <OperationIcon v-show='permission.view' type='info' content='历史记录' placement='top-start' icon-name='el-icon-info' 
                    @click='onShowHistory(scope.$index, scope.row)'></OperationIcon>
                </template>
              </el-table-column>
            </el-table>
    	  </div>	          
        </el-col>
      </el-row>
      <!-- 表格栏  结束 -->
    </div>
  </el-row>
</template>

<script>
import { validatenull } from '@/utils/validate'
import { treeCompany, getCompanyById, deleteCompany } from '@/api/org/company'
import { listResourcePermission } from '@/api/admin/common/permission'
import CompanyForm from './companyForm'
import { listClinicVersionAll } from '@/api/clinic/clinicVersion'
import ExportExcelButton from '@/components/ExportExcelButton'
import ViewColumnsSelect from '@/views/components/ViewColumnsSelect'
import QueryForm from '@/views/components/queryForm'
import MainUI from '@/views/components/mainUI'
import OperationIcon from '@/components/OperationIcon'
import History from '@/views/components/history'
export default {
  extends: MainUI,
  components: { 
    CompanyForm,
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
        'phone_number': '=',
        'version_id': '=',
      },
      queryModel: {
        'name': '',   // 名称
        'phoneNumber': '',   // 电话
        'version': {     // 诊所版本
          'id': null,
          'name': '',
        },
      },
      search: {
        params: [],    
        offset: 0,
        limit: 10,
		columnName: '',      // 排序字段名
        order: ''            // 排序
      },
      companyList: [],
        
        version_List: [],    // 诊所版本
      
      oprColumnWidth: 160,  // 操作列宽
      tableId: '41040096140492800',
      schemeId: '41040096140492817'
    }
  },
  methods: {
    getCompanyList() {
      this.setLoad()
      /* 查询参数 和数据权限 */
      this.search.params = []
      if(this.moreCodition) {
        this.search.params = this.search.params.concat(this.compositeCondition())
      }else{
        // 查询参数: 名称
        this.search.params.push({
      	  columnName: 'name',
      	  queryType: 'like',
          value: this.queryModel.name
        })
        // 查询参数: 电话
        this.search.params.push({
      	  columnName: 'phone_number',
      	  queryType: '=',
          value: this.queryModel.phoneNumber
        })
        // 查询参数: 诊所版本
        this.search.params.push({
      	  columnName: 'version_id',
      	  queryType: '=',
          value: validatenull(this.queryModel.version.id) ? '' : this.queryModel.version.id
        })
      }
      // 数据权限: 公司org_company
      this.pushDataPermissions(this.search.params, this.$route.meta.routerId, this.tableId)
      treeCompany(this.search).then(responseData => {
        if(responseData.code == 100) {
          this.companyList = responseData.data
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
        this.getCompanyList()
      } else {
        this.$refs['queryForm'].validate(valid => {
          if (valid) {
            this.getCompanyList()
          } else {
            return false
          }
        })
      }
    },
    async pageInit() {
      this.setLoad()
      try {
        this.initOptions(this.queryModel)
        this.search.params = []
        // 数据权限: 公司org_company
        this.pushDataPermissions(this.search.params, this.$route.meta.routerId, this.tableId)
        let [listCompanyRespData, listPermissionRespData] = await Promise.all([
          treeCompany(this.search),
          listResourcePermission(this.$route.meta.routerId)
        ])
        if(listCompanyRespData.code == 100 && listPermissionRespData.code == 100) {
          this.companyList = listCompanyRespData.data
          this.permission.view = listPermissionRespData.data.find(item => {
            return item.permission === 'company:read'
          })
          this.permission.export = listPermissionRespData.data.find(item => {
            return item.permission === 'company:export'
          })
          this.permission.add = listPermissionRespData.data.find(item => {
            return item.permission === 'company:create'
          })
          this.permission.edit = listPermissionRespData.data.find(item => {
            return item.permission === 'company:update'
          })
          this.permission.remove = listPermissionRespData.data.find(item => {
            return item.permission === 'company:delete'
          })
        } else {
          this.showMessage(listPermissionRespData.code != 100 ? listPermissionRespData : listCompanyRespData)
        }
        this.resetLoad()
      } catch(error) {
        this.outputError(error) 
      }
    },
    onViewCompany(index, row) {
      this.setLoad()
      getCompanyById(row.id).then(responseData => {
        if(responseData.code == 100) {
          let company = responseData.data
          if(validatenull(company.parent)) {
            company.parent = {id: null}
          }        
          this.$refs.companyForm.$emit('openViewCompanyDialog', company)
        } else {
          this.showMessage(responseData)
        }
        this.resetLoad()
      }).catch(error => {
        this.outputError(error)
      })
    },
    onCreateCompany(index, row) {
      this.$refs.companyForm.$emit('openAddCompanyDialog', row)
    },
    onEditCompany(index, row) {
      this.setLoad()
      getCompanyById(row.id).then(responseData => {
        if(responseData.code == 100) {
          let company = responseData.data
          if(validatenull(company.parent)) {
            company.parent = {id: null}
          }
          this.$refs.companyForm.$emit('openEditCompanyDialog', company)
        }else{
          this.showMessage(responseData)
        }
        this.resetLoad()
      }).catch(error => {
        this.outputError(error)
      })
    },
    onCopyCompany(index, row) {
      this.setLoad()
      getCompanyById(row.id).then(responseData => {
        if(responseData.code == 100) {
          let company = responseData.data
          if(validatenull(company.parent)) {
            company.parent = {id: null}
          }        
          this.$refs.companyForm.$emit('openCopyCompanyDialog', company)
        } else {
          this.showMessage(responseData)
        }
        this.resetLoad()
      }).catch(error => {
        this.outputError(error)
      })
    },
    onDeleteCompany(index, row) {
      this.$confirm('确定删除吗？', '确认', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.setLoad()
        deleteCompany(row).then(responseData => {
          if(responseData.code == 100) {
            this.getCompanyList()
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

      this.getCompanyList()
    },
    initOptions(This) {
      let version_search = {
        params: []
      }
      // 响应字段的条件操作符，替换成触发字段的操作符
      version_search.params.forEach(item => {
        if(this.queryTypes[item.columnName]) {
          item.queryType = this.queryTypes[item.columnName]
        }
      })
      // 字段对应表上filter条件
        version_search.params.push.apply(version_search.params, [{columnName: 'company_id', queryType: '=', value: function() {var user = JSON.parse(sessionStorage.getItem('currentUser')); return user.company.id;}()}])
      // 数据权限: 诊所版本clinic_version
      this.pushDataPermissions(version_search.params, this.$route.meta.routerId, '987744398207139863')
      this.version_List.splice(0, this.version_List.length)
      listClinicVersionAll(version_search).then(responseData => {
        this.version_List = responseData.data
      })
    } 
  },
  watch: {
  },
  mounted() {
    this.pageInit()
  }
}
</script>