<template>
  <el-row v-loading='loading'>
    <!-- 历史记录  -->
    <History :bussObject='curentRow' ></History>
    <!-- 编辑窗口  -->
    <dictType-form ref='dictTypeForm' :permission='permission' v-on:save-finished='getDictTypeList()'></dictType-form>
    <div class="page-container">
      <!--  搜索栏  开始 -->
      <div class='query-form-container'>
          <el-row v-if='!moreCodition' class='search-row'>
            <el-form :model='queryModel' @submit.native.prevent label-position="left" label-width='70px' ref='queryForm' :inline-message='true'>
              <el-col :span="6">
                <el-form-item label='字典类型名' prop='name' label-width="90px">
                  <el-input v-model='queryModel.name' :clearable='true' placeholder='请输入字典类型名'></el-input>
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
          <el-button type='primary' :plain='true' @click='checkFile'>导入</el-button>
          <input type="file" id="fileinput" style="display: none;" @change="uploadExcel"/>
          <el-button v-show='permission.add' type='primary' icon='el-icon-plus'  @click='onCreateDictType()'>添加</el-button>
        </div>
      </div>
      <!-- 工具栏 结束 -->
      <!-- 表格栏  开始 -->
      <el-row>
        <el-col :span='24'>
          <div @mouseleave='moveTableOutside'>
            <el-table class='drag_table' :data='dictTypeList' border @sort-change='onSortChange' @header-dragend='onChangeWidth' :cell-class-name='cellClassName' :header-cell-class-name='headerCellClassName' highlight-current-row>
              <el-table-column v-for="(cv, index) in columnViews" v-if='cv.display' :prop='cv.prop' :key="`columnViews_${index}`" :label='cv.label' sortable='custom' :align='cv.align' :min-width='cv.miniWidth+"px"' :width='cv.width+"px"' header-align='center' :column-key='index.toString()' :render-header="renderHeader">
                <template slot-scope='{row,$index}'>
                  <span v-if='columnViews[index].showType == "Switch" || columnViews[index].showType == "Checkbox" || columnViews[index].showType == "Radio"'>
                    <li v-if='getAttrValue(row, columnViews[index].prop) == "1"' class='el-icon-check' style='color:#F56C6C;'></li>
                  </span>
                  <span v-else>{{ getAttrValue(row, columnViews[index].prop, columnViews[index].javaType)}}</span>
                </template>
              </el-table-column>
              <!--表行级操作按钮-->
              <el-table-column label='操作' header-align='center' :width='oprColumnWidth + "px"' fixed='right'>
                <template slot='header' slot-scope="scope">
                  <span>操作</span>
                  <view-columns-select v-model='columnViews' v-on:save-column-view='saveColumn' v-on:show-all-column='showAllColumn' v-on:show-default-column='showDefaultColumn'></view-columns-select>
                  <export-excel-button v-show='permission.export' :data='dictTypeList' :tHeader='getHeads()' :filterVal='getFilterVal()' :plain='true'></export-excel-button>
                </template>
                <template slot-scope='scope'>
                  <OperationIcon v-show='permission.view' type='info' content='查看' placement='top-start' icon-name='el-icon-view'
                    @click='onViewDictType(scope.$index, scope.row)'></OperationIcon>
                  <OperationIcon v-show='permission.edit' type='primary' content='编辑' placement='top-start' icon-name='el-icon-edit'
                    @click='onEditDictType(scope.$index, scope.row)'></OperationIcon>
                  <OperationIcon v-show='permission.add' type='primary' content='复制' placement='top-start' icon-name='el-icon-document'
                    @click='onCopyDictType(scope.$index, scope.row)'></OperationIcon>
                  <OperationIcon v-show='permission.remove' type='danger' content='删除' placement='top-start' icon-name='el-icon-delete'
                    @click='onDeleteDictType(scope.$index, scope.row)'></OperationIcon>
                  <OperationIcon v-show='permission.view' type='info' content='历史记录' placement='top-start' icon-name='el-icon-info'
                    @click='onShowHistory(scope.$index, scope.row)'></OperationIcon>
                  <OperationIcon type='info' content='导出' placement='top-start' icon-name='el-icon-download'
                                 @click='onExportExcel(scope.$index, scope.row)'></OperationIcon>
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
            :page-sizes='[10, 20, 50, 100, dictTypeTotal]'
            :page-size='10'
            layout='total, sizes, prev, pager, next, jumper'
            :total='dictTypeTotal'>
          </el-pagination>
        </el-col>
      </el-row>
      <!-- 分页栏     结束 -->
    </div>
  </el-row>
</template>

<script>
import FileSaver from 'file-saver'
import { validatenull } from '@/utils/validate'
import { listDictTypePage, getDictTypeById, deleteDictType, importDictType} from '@/api/sys/dictType'
import { listResourcePermission } from '@/api/admin/common/permission'
import DictTypeForm from './dictTypeForm'
import ExportExcelButton from '@/components/ExportExcelButton'
import ViewColumnsSelect from '@/views/components/ViewColumnsSelect'
import QueryForm from '@/views/components/queryForm'
import MainUI from '@/views/components/mainUI'
import OperationIcon from '@/components/OperationIcon'
import History from '@/views/components/history'
export default {
  extends: MainUI,
  components: {
    DictTypeForm,
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
        'name': '',   // 字典类型名
      },
      search: {
        params: [],
        offset: 0,
        limit: 10,
        columnName: '',       // 排序字段名
        order: ''             // 排序
      },
      currentPage: 1,
      dictTypeTotal: 0,
      dictTypeList: [],


      oprColumnWidth: 165,  // 操作列宽
      tableId: '4002',
      schemeId: '6003'
    }
  },
  methods: {
    checkFile() {
      document.querySelector('#fileinput').click()
    },

    uploadExcel(evt){
      const files = evt.target.files;
      if(files==null || files.length==0){
        alert("No files wait for import");
        return;
      }

      let name = files[0].name;
      let suffixArr = name.split("."), suffix = suffixArr[suffixArr.length-1];
      if(suffix!="json"){
        alert("Currently only supports the import of json files");
        return;
      }

      const reader = new FileReader()
      reader.readAsText(files[0])

      const _this = this
      reader.onload = function () {
        _this.ImportJSON = JSON.parse(this.result)
        // 检测是否导入成功
        console.log(_this.ImportJSON)
        _this.ImportJson(_this.ImportJSON)
        document.getElementById("fileinput").value = "";

      }
    },

    ImportJson(row) {
      this.setLoad()
      importDictType(row).then(responseData => {
        if(responseData.code == 100) {
          this.$message({
            message: '导入成功',
            type: 'info'
          });
          this.getDictTypeList()
        } else {
          this.showMessage(responseData)
        }
        this.resetLoad()
      }).catch(error => {
        this.outputError(error)
      })

    },

    getDictTypeList() {
      this.setLoad()
      /* 查询参数 和数据权限 */
      this.search.params = []
      // 如果不是超级用户，列表仅显示非系统级的系统用户
      if(currentUser.id != 1000) {
        this.search.params.push({columnName: 'is_system', queryType: '=', value: '0'})
      }
      if(this.moreCodition) {
        this.search.params = this.search.params.concat(this.compositeCondition())
      }else{
        // 查询参数: 字典类型名
        this.search.params.push({
      	  columnName: 'name',
      	  queryType: 'like',
          value: this.queryModel.name
        })
      }
      // 数据权限: 字典类型sys_dict_type
      this.pushDataPermissions(this.search.params, this.$route.meta.routerId, this.tableId)
      listDictTypePage(this.search).then(responseData => {
        if(responseData.code == 100) {
          this.dictTypeTotal = responseData.data.total
          this.dictTypeList = responseData.data.rows
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
        this.getDictTypeList()
      } else {
        this.$refs['queryForm'].validate(valid => {
          if (valid) {
            this.search.offset = 0
            this.currentPage = 1
            this.getDictTypeList()
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
      this.getDictTypeList()
    },
    onCurrentChange(val) {
      this.search.offset = (val - 1) * this.search.limit
      this.currentPage = val
      this.getDictTypeList()
    },
    async pageInit() {
      this.setLoad()
      try {
        this.initOptions(this.queryModel)
        this.search.params = []
        // 如果不是超级用户，列表仅显示非系统级的系统用户
        if(currentUser.id != 1000) {
          this.search.params.push({columnName: 'is_system', queryType: '=', value: '0'})
        }
        // 数据权限: 字典类型sys_dict_type
        this.pushDataPermissions(this.search.params, this.$route.meta.routerId, this.tableId)
        let [listDictTypeRespData, listPermissionRespData] = await Promise.all([
          listDictTypePage(this.search),
          listResourcePermission(this.$route.meta.routerId)
        ])
        if(listDictTypeRespData.code == 100 && listPermissionRespData.code == 100) {
          this.dictTypeTotal = listDictTypeRespData.data.total
          this.dictTypeList = listDictTypeRespData.data.rows
          this.permission.view = listPermissionRespData.data.find(item => {
            return item.permission === 'dictType:read'
          })
          this.permission.export = listPermissionRespData.data.find(item => {
            return item.permission === 'dictType:export'
          })
          this.permission.add = listPermissionRespData.data.find(item => {
            return item.permission === 'dictType:create'
          })
          this.permission.edit = listPermissionRespData.data.find(item => {
            return item.permission === 'dictType:update'
          })
          this.permission.remove = listPermissionRespData.data.find(item => {
            return item.permission === 'dictType:delete'
          })
        } else {
          this.showMessage(listPermissionRespData.code != 100 ? listPermissionRespData : listDictTypeRespData)
        }
        this.resetLoad()
      } catch(error) {
        this.outputError(error)
      }
    },
    onViewDictType(index, row) {
      this.setLoad()
      getDictTypeById(row.id).then(responseData => {
        if(responseData.code == 100) {
          this.$refs.dictTypeForm.$emit('openViewDictTypeDialog', responseData.data)
        } else {
          this.showMessage(responseData)
        }
        this.resetLoad()
      }).catch(error => {
        this.outputError(error)
      })
    },
    onCreateDictType() {
      this.$refs.dictTypeForm.$emit('openAddDictTypeDialog')
    },
    onEditDictType(index, row) {
      this.setLoad()
      getDictTypeById(row.id).then(responseData => {
        if(responseData.code == 100) {
          this.$refs.dictTypeForm.$emit('openEditDictTypeDialog', responseData.data)
        }else{
          this.showMessage(responseData)
        }
        this.resetLoad()
      }).catch(error => {
        this.outputError(error)
      })
    },
    onCopyDictType(index, row) {
      this.setLoad()
      getDictTypeById(row.id).then(responseData => {
        if(responseData.code == 100) {
          this.$refs.dictTypeForm.$emit('openCopyDictTypeDialog', responseData.data)
        } else {
          this.showMessage(responseData)
        }
        this.resetLoad()
      }).catch(error => {
        this.outputError(error)
      })
    },
    onDeleteDictType(index, row) {
      this.$confirm('确定删除吗？', '确认', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.setLoad()
        deleteDictType(row).then(responseData => {
          if(responseData.code == 100) {
            this.getDictTypeList()
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

      this.getDictTypeList()
    },
    onExportExcel(index, row) {
      this.setLoad()
      getDictTypeById(row.id).then(responseData => {
        if(responseData.code == 100) {
          const data = JSON.stringify(responseData.data)
          const blob = new Blob([data], {type: ''})
          FileSaver.saveAs(blob, 'dictType.json')
        } else {
          this.showMessage(responseData)
        }
        this.resetLoad()
      }).catch(error => {
        this.outputError(error)
      })
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
