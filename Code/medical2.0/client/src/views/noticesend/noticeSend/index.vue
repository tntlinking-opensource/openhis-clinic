<template>
  <el-row v-loading='loading'>
    <!-- 历史记录  -->
    <History :bussObject='curentRow' ></History>
    <!-- 编辑窗口  -->
    <noticeSend-form ref='noticeSendForm' :permission='permission' v-on:save-finished='getNoticeSendList()'></noticeSend-form>
    <el-col :span='24'>
      <!--  搜索栏  开始 -->
      <el-container class='query-form-container'>
        <el-main>
          <el-row v-if='!moreCodition' class='search-row'>
            <el-form :model='queryModel' @submit.native.prevent label-position="right" label-width='100px' ref='queryForm' :inline-message='true'>
                  <el-col :span="6">
              <el-form-item label='标题' prop='title'>
                <el-input v-model='queryModel.title' :clearable='true' placeholder='请输入标题'></el-input>
              </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label-width="20px">
              <el-button type='primary' icon='el-icon-search' @click='onSearch()' :plain='true'>搜索</el-button>
              <el-button type='info' icon='el-icon-refresh-left' @click='$refs.queryForm.resetFields()' :plain='true'>重置</el-button>
              <el-button type='info' icon='el-icon-d-arrow-right' @click='onMoreCodition()' :plain='true'>更多</el-button>
                </el-form-item>
              </el-col>
            </el-form>
          </el-row>
          <QueryForm v-else v-model='moreParm' :tableId='tableId' :schemeId='schemeId'  :routerId='$route.meta.routerId' @search='onSearch()' @moreCodition='onMoreCodition()'></QueryForm>
        </el-main>
        <el-aside align="right" width="90px">
          <el-button v-show='permission.add' type='primary' icon='el-icon-plus'  @click='onCreateNoticeSend()'>添加</el-button>
        </el-aside>
      </el-container>
      <!--  搜索栏  结束 -->
      <el-container class="data-container">
        <el-main>
      <!-- 表格栏  开始 -->
      <el-row>
        <el-col :span='24'>
          <div @mouseleave='moveTableOutside'>        
            <el-table class='drag_table' :data='noticeSendList' border @sort-change='onSortChange' @header-dragend='onChangeWidth' :cell-class-name='cellClassName' :header-cell-class-name='headerCellClassName' highlight-current-row>                
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
                  <export-excel-button v-show='permission.export' :data='noticeSendList' :tHeader='getHeads()' :filterVal='getFilterVal()' :plain='true'></export-excel-button>
                </template>
                <template slot-scope='scope'>
                  <OperationIcon v-show='permission.view' type='info' content='查看' placement='top-start' icon-name='el-icon-view' 
                    @click='onViewNoticeSend(scope.$index, scope.row)'></OperationIcon>
                  <OperationIcon v-show='permission.edit' type='primary' content='编辑' placement='top-start' icon-name='el-icon-edit' 
                    @click='onEditNoticeSend(scope.$index, scope.row)'></OperationIcon>
                  <OperationIcon v-show='permission.add' type='primary' content='复制' placement='top-start' icon-name='el-icon-document' 
                    @click='onCopyNoticeSend(scope.$index, scope.row)'></OperationIcon>
                  <OperationIcon v-show='permission.remove' type='danger' content='删除' placement='top-start' icon-name='el-icon-delete' 
                    @click='onDeleteNoticeSend(scope.$index, scope.row)'></OperationIcon>
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
            :page-sizes='[10, 20, 50, 100, noticeSendTotal]'
            :page-size='10'
            layout='total, sizes, prev, pager, next, jumper'
            :total='noticeSendTotal'>
          </el-pagination>
        </el-col>
      </el-row>
      <!-- 分页栏     结束 -->       	</el-main>
      </el-container>
    </el-col>
  </el-row>
</template>

<script>
import { validatenull } from '@/utils/validate'
import { listNoticeSendPage, getNoticeSendById, deleteNoticeSend } from '@/api/noticesend/noticeSend'
import { listResourcePermission } from '@/api/admin/common/permission'
import NoticeSendForm from './noticeSendForm'
import ExportExcelButton from '@/components/ExportExcelButton'
import ViewColumnsSelect from '@/views/components/ViewColumnsSelect'
import QueryForm from '@/views/components/queryForm'
import MainUI from '@/views/components/mainUI'
import OperationIcon from '@/components/OperationIcon'
import History from '@/views/components/history'
export default {
  extends: MainUI,
  components: { 
    NoticeSendForm,
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
        'title': 'like',
      },
      queryModel: {
        'title': '',   // 标题
      },
      search: {
        params: [],    
        offset: 0,
        limit: 10,
        columnName: '',       // 排序字段名
        order: ''             // 排序
      },
      currentPage: 1,
      noticeSendTotal: 0,
      noticeSendList: [],
        
      
      oprColumnWidth: 140,  // 操作列宽
      tableId: '723817798886907905',
      schemeId: '723817798886907925'
    }
  },
  methods: {
    getNoticeSendList() {
      this.setLoad()
      /* 查询参数 和数据权限 */
      this.search.params = []
      if(this.moreCodition) {
        this.search.params = this.search.params.concat(this.compositeCondition())
      }else{
        // 查询参数: 标题
        this.search.params.push({
      	  columnName: 'title',
      	  queryType: 'like',
          value: this.queryModel.title
        })
      }
      // 数据权限: 公告发送记录 notice_send
      this.pushDataPermissions(this.search.params, this.$route.meta.routerId, this.tableId)
      listNoticeSendPage(this.search).then(responseData => {
        if(responseData.code == 100) {
          this.noticeSendTotal = responseData.data.total
          this.noticeSendList = responseData.data.rows
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
        this.getNoticeSendList()
      } else {
        this.$refs['queryForm'].validate(valid => {
          if (valid) {
            this.search.offset = 0
            this.currentPage = 1
            this.getNoticeSendList()
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
      this.getNoticeSendList()
    },
    onCurrentChange(val) {
      this.search.offset = (val - 1) * this.search.limit
      this.currentPage = val
      this.getNoticeSendList()
    },
    async pageInit() {
      this.setLoad()
      try {
        this.initOptions(this.queryModel)
        this.search.params = []
        // 数据权限: 公告发送记录 notice_send
        this.pushDataPermissions(this.search.params, this.$route.meta.routerId, this.tableId)
        let [listNoticeSendRespData, listPermissionRespData] = await Promise.all([
          listNoticeSendPage(this.search),
          listResourcePermission(this.$route.meta.routerId)
        ])
        if(listNoticeSendRespData.code == 100 && listPermissionRespData.code == 100) {
          this.noticeSendTotal = listNoticeSendRespData.data.total
          this.noticeSendList = listNoticeSendRespData.data.rows
          this.permission.view = listPermissionRespData.data.find(item => {
            return item.permission === 'noticeSend:read'
          })
          this.permission.export = listPermissionRespData.data.find(item => {
            return item.permission === 'noticeSend:export'
          })
          this.permission.add = listPermissionRespData.data.find(item => {
            return item.permission === 'noticeSend:create'
          })
          this.permission.edit = listPermissionRespData.data.find(item => {
            return item.permission === 'noticeSend:update'
          })
          this.permission.remove = listPermissionRespData.data.find(item => {
            return item.permission === 'noticeSend:delete'
          })
        } else {
          this.showMessage(listPermissionRespData.code != 100 ? listPermissionRespData : listNoticeSendRespData)
        }
        this.resetLoad()
      } catch(error) {
        this.outputError(error) 
      }
    },
    onViewNoticeSend(index, row) {
      this.setLoad()
      getNoticeSendById(row.id).then(responseData => {
        if(responseData.code == 100) {
          this.$refs.noticeSendForm.$emit('openViewNoticeSendDialog', responseData.data)
        } else {
          this.showMessage(responseData)
        }
        this.resetLoad()
      }).catch(error => {
        this.outputError(error)
      })
    },
    onCreateNoticeSend() {
      this.$refs.noticeSendForm.$emit('openAddNoticeSendDialog')
    },
    onEditNoticeSend(index, row) {
      this.setLoad()
      getNoticeSendById(row.id).then(responseData => {
        if(responseData.code == 100) {
          this.$refs.noticeSendForm.$emit('openEditNoticeSendDialog', responseData.data)
        }else{
          this.showMessage(responseData)
        }
        this.resetLoad()
      }).catch(error => {
        this.outputError(error)
      })
    },
    onCopyNoticeSend(index, row) {
      this.setLoad()
      getNoticeSendById(row.id).then(responseData => {
        if(responseData.code == 100) {
          this.$refs.noticeSendForm.$emit('openCopyNoticeSendDialog', responseData.data)
        } else {
          this.showMessage(responseData)
        }
        this.resetLoad()
      }).catch(error => {
        this.outputError(error)
      })
    },
    onDeleteNoticeSend(index, row) {
      this.$confirm('确定删除吗？', '确认', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.setLoad()
        deleteNoticeSend(row).then(responseData => {
          if(responseData.code == 100) {
            this.getNoticeSendList()
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

      this.getNoticeSendList()
    },
    initOptions(This) {
    } 
  },
  watch: {
  },
  mounted() {
    this.pageInit()
  }
}
</script>
