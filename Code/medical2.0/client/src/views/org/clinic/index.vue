<template>
  <el-row v-loading='loading'>
    <!-- 历史记录  -->
    <History :bussObject='curentRow' ></History>
    <!-- 编辑窗口  -->
    <clinic-form ref='clinicForm' :permission='permission' v-on:save-finished='getClinicList()'></clinic-form>
    <div class="page-container">
        <!--  搜索栏  开始 -->
        <div class='query-form-container'>
          <el-row v-if='!moreCodition' class='search-row'>
            <el-form :model='queryModel' @submit.native.prevent label-position="left" label-width='70px' ref='queryForm' :inline-message='true'>
                      <el-col :span="6">
              <el-form-item label='诊所名称' prop='name'>
                <el-input v-model='queryModel.name' :clearable='true' placeholder='请输入诊所名称'></el-input>
              </el-form-item>
            </el-col>
              <el-col :span="6">
              <el-form-item label='电话' prop='phoneNumber'>
                <el-input v-model='queryModel.phoneNumber' :clearable='true' placeholder='请输入电话'></el-input>
              </el-form-item>
            </el-col>
              <el-col :span="6">
              <el-form-item label='产品版本' prop='version'>
                <el-select v-model='queryModel.version' value-key='id'  filterable clearable placeholder='请选择产品版本'>
                   <el-option v-for='version in version_List' :key='version.id' :label='version.name' :value='version'></el-option>
                </el-select>
              </el-form-item>
            </el-col>
              <el-col :span="6">
              <el-form-item label='状态' prop='status'>
                <el-switch v-model='queryModel.status' active-color='#13ce66' inactive-color='#dbdfe6' :active-value='1' :inactive-value='0'></el-switch>
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
            <el-button v-show='permission.add' type='primary' icon='el-icon-plus'  @click='onCreateClinic()'>添加</el-button>
          </div>
        </div>
        <!-- 工具栏 结束 -->

      <!-- 表格栏  开始 -->
      <el-row>
        <el-col :span='24'>
          <div @mouseleave='moveTableOutside'>        
            <el-table class='drag_table' :data='clinicList' border @sort-change='onSortChange' @header-dragend='onChangeWidth' :cell-class-name='cellClassName' :header-cell-class-name='headerCellClassName' highlight-current-row>                
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
                  <export-excel-button v-show='permission.export' :data='clinicList' :tHeader='getHeads()' :filterVal='getFilterVal()' :plain='true'></export-excel-button>
                </template>
                <template slot-scope='scope'>
                  <OperationIcon v-show='permission.view' type='info' content='查看' placement='top-start' icon-name='el-icon-view' 
                    @click='onViewClinic(scope.$index, scope.row)'></OperationIcon>
                  <OperationIcon v-show='permission.edit' type='primary' content='编辑' placement='top-start' icon-name='el-icon-edit' 
                    @click='onEditClinic(scope.$index, scope.row)'></OperationIcon>
                  <OperationIcon v-show='permission.add' type='primary' content='复制' placement='top-start' icon-name='el-icon-document' 
                    @click='onCopyClinic(scope.$index, scope.row)'></OperationIcon>
                  <OperationIcon v-show='permission.remove' type='danger' content='删除' placement='top-start' icon-name='el-icon-delete' 
                    @click='onDeleteClinic(scope.$index, scope.row)'></OperationIcon>
                  <OperationIcon v-show='permission.view' type='info' content='历史记录' placement='top-start' icon-name='el-icon-info' 
                    @click='onShowHistory(scope.$index, scope.row)'></OperationIcon>
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
            :page-sizes='[10, 20, 50, 100, clinicTotal]'
            :page-size='10'
            layout='total, sizes, prev, pager, next, jumper'
            :total='clinicTotal'>
          </el-pagination>
        </el-col>
      </el-row>
      <!-- 分页栏     结束 -->     </div>
  </el-row>
</template>

<script>
import { validatenull } from '@/utils/validate'
import { listClinicPage, getClinicById, deleteClinic } from '@/api/org/clinic'
import { listResourcePermission } from '@/api/admin/common/permission'
import ClinicForm from './clinicForm'
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
    ClinicForm,
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
    	'lessee_id': '=',
        'phone_number': 'like',
        'version_id': '=',
        'status': '=',
      },
      //---查询参数
      queryModel: {
        'name': '',   // 诊所名称
        'lesseeId': {     // 父表 租户
          'id': null,
          'userName': '',
        },
        'phoneNumber': '',   // 电话
        'version': {     // 产品版本
          'id': null,
          'name': '',
        },
        'status': 1
      },
      search: {
        params: [],    
        offset: 0,
        limit: 10,
		columnName: '',      // 排序字段名
        order: ''            // 排序
      },
      currentPage: 1,
      clinicTotal: 0,
      clinicList: [],
        
        version_List: [],    // 产品版本
      
      oprColumnWidth: 140,  // 操作列宽
      tableId: '41040096140492800',
      schemeId: '986498806151577787'
    }
  },
  methods: {
    getClinicList() {
      this.setLoad()
      /* 查询参数 和数据权限 */
      this.search.params = []
      if(this.moreCodition) {
        this.search.params = this.search.params.concat(this.compositeCondition())
      }else{
        // 查询参数: 诊所名称
        this.search.params.push({
      	  columnName: 'name',
      	  queryType: 'like',
          value: this.queryModel.name
        })
        // 查询参数: 电话
        this.search.params.push({
      	  columnName: 'phone_number',
      	  queryType: 'like',
          value: this.queryModel.phoneNumber
        })
        // 查询参数: 产品版本
        this.search.params.push({
      	  columnName: 'version_id',
      	  queryType: '=',
          value: validatenull(this.queryModel.version.id) ? '' : this.queryModel.version.id
        })
        // 查询参数: 状态
        this.search.params.push({
      	  columnName: 'status',
      	  queryType: '=',
          value: this.queryModel.status
        })
      }
      // 数据权限: 诊所clinic
      this.pushDataPermissions(this.search.params, this.$route.meta.routerId, this.tableId)
      listClinicPage(this.search).then(responseData => {
        if(responseData.code == 100) {
          this.clinicTotal = responseData.data.total
          this.clinicList = responseData.data.rows
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
        this.getClinicList()
      } else {
        this.$refs['queryForm'].validate(valid => {
          if (valid) {
            this.search.offset = 0
            this.currentPage = 1
            this.getClinicList()
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
      this.getClinicList()
    },
    onCurrentChange(val) {
      this.search.offset = (val - 1) * this.search.limit
      this.currentPage = val
      this.getClinicList()
    },
    //初始化页面
    async pageInit() {
      this.setLoad()
      try {
        this.initOptions(this.queryModel)
        this.search.params = []
        // 数据权限: 诊所clinic
        this.pushDataPermissions(this.search.params, this.$route.meta.routerId, this.tableId)
        let [listClinicRespData, listPermissionRespData] = await Promise.all([
          listClinicPage(this.search),
          listResourcePermission(this.$route.meta.routerId)
        ])
        if(listClinicRespData.code == 100 && listPermissionRespData.code == 100) {
          
          this.clinicTotal = listClinicRespData.data.total
          this.clinicList = listClinicRespData.data.rows
          this.permission.view = listPermissionRespData.data.find(item => {
            return item.permission === 'clinic:read'
          })
          this.permission.export = listPermissionRespData.data.find(item => {
            return item.permission === 'clinic:export'
          })
          this.permission.add = listPermissionRespData.data.find(item => {
            return item.permission === 'clinic:create'
          })
          this.permission.edit = listPermissionRespData.data.find(item => {
            return item.permission === 'clinic:update'
          })
          this.permission.remove = listPermissionRespData.data.find(item => {
            return item.permission === 'clinic:delete'
          })
        } else {
          this.showMessage(listPermissionRespData.code != 100 ? listPermissionRespData : listClinicRespData)
        }
        this.resetLoad()
      } catch(error) {
        this.outputError(error) 
      }
    },
    //弹窗查看方法
    onViewClinic(index, row) {
      this.setLoad()
      getClinicById(row.id).then(responseData => {
        if(responseData.code == 100) {
          this.$refs.clinicForm.$emit('openViewClinicDialog', responseData.data)
        } else {
          this.showMessage(responseData)
        }
        this.resetLoad()
      }).catch(error => {
        this.outputError(error)
      })
    },
    onCreateClinic() {
      this.$refs.clinicForm.$emit('openAddClinicDialog')
    },
    onEditClinic(index, row) {
      this.setLoad()
      getClinicById(row.id).then(responseData => {
        if(responseData.code == 100) {
          this.$refs.clinicForm.$emit('openEditClinicDialog', responseData.data)
        }else{
          this.showMessage(responseData)
        }
        this.resetLoad()
      }).catch(error => {
        this.outputError(error)
      })
    },
    onCopyClinic(index, row) {
      this.setLoad()
      getClinicById(row.id).then(responseData => {
        if(responseData.code == 100) {
          this.$refs.clinicForm.$emit('openCopyClinicDialog', responseData.data)
        } else {
          this.showMessage(responseData)
        }
        this.resetLoad()
      }).catch(error => {
        this.outputError(error)
      })
    },
    onDeleteClinic(index, row) {
      this.$confirm('确定删除吗？', '确认', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.setLoad()
        deleteClinic(row).then(responseData => {
          if(responseData.code == 100) {
            this.getClinicList()
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

      this.getClinicList()
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