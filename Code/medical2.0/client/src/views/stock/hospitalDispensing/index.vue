<template>
  <el-row v-loading='loading'>
    <el-card class="page-container">
      <!--  搜索栏  开始 -->
      <div class='query-form-container'>
        <el-row v-if='!moreCodition' class='search-row'>
          <el-form :model='queryModel' @submit.native.prevent label-position="left" label-width='70px' ref='queryForm'
            :inline-message='true'>
            <el-col :span="5">
              <el-form-item label='药品名称' prop='keyword'>
                <el-input v-model='queryModel.keyword' :clearable='true' placeholder='请输入材料名称'></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="5">
              <el-form-item label='是否同步' prop='zstbzt'>
                <el-select v-model="queryModel.zstbzt" placeholder="请选择是否启用">
                  <el-option v-for="item in zstbztOptions" :key="item.value" :label="item.label" :value="item.value">
                  </el-option>
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label='发药时间'>
                <el-date-picker v-model="dispensingTime" type="daterange" range-separator="至" start-placeholder="开始日期"
                  end-placeholder="结束日期">
                </el-date-picker>
              </el-form-item>
            </el-col>
            <el-col :span="3" style="display:flex;justivy-content:space-around">
              <el-button type="primary" icon="el-icon-search" @click="onSearch()" :plain="true">搜索
              </el-button>
              <el-button type="info" icon="el-icon-refresh-left" @click="reset" :plain="true">重置
              </el-button>
              <el-button style="margin-right: 10px;" type="primary" icon="el-icon-refresh" @click="onSynchronous()">同步
                </el-button>
            </el-col>
          </el-form>
        </el-row>
        <QueryForm v-else v-model='moreParm' :tableId='tableId' :schemeId='schemeId' :routerId='$route.meta.routerId'
          @search='onSearch()' @moreCodition='onMoreCodition()'></QueryForm>
      </div>
      <!--  搜索栏  结束 -->

      <!-- 工具栏 开始 -->

      <!-- 工具栏 结束 -->

      <!-- 表格栏  开始 -->
      <el-row>
        <el-col :span='24'>
          <div @mouseleave='moveTableOutside' style="height:">
            <el-table ref="tableRef" height="calc(100vh - 280px)" class='drag_table' :data='stuffList' border
              @sort-change='onSortChange' @header-dragend='onChangeWidth' :cell-class-name='cellClassName'
              :header-cell-class-name='headerCellClassName' highlight-current-row @selection-change="handleSelectionChange">
              <el-table-column type='selection' :reserve-selection="true" width='50' align='center'
                :selectable="selectable">
              </el-table-column>
              <el-table-column label="序号" type="index" :index="indexMethod" align="center">
              </el-table-column>
              <el-table-column v-for="item in columnList" :label="item.label" :prop="item.prop"></el-table-column>
            </el-table>
          </div>
        </el-col>
      </el-row>
      <!-- 表格栏  结束 -->
      <!-- 分页栏     开始 -->
      <el-row>
        <el-col :span='24'>
          <el-pagination background @size-change='onSizeChange' @current-change='onCurrentChange'
            :current-page.sync='currentPage' :page-sizes='[20, 50, 100, stuffTotal]' :page-size='20'
            layout='total, sizes, prev, pager, next, jumper' :total='stuffTotal'>
          </el-pagination>
        </el-col>
      </el-row>
      <!-- 分页栏     结束 -->
    </el-card>
  </el-row>
</template>

<script>
import { validatenull } from '@/utils/validate'
import { listInstitutionDispensingPage, syncInstitutionDispensing } from '@/api/stock/stuff'
import { listResourcePermission } from '@/api/admin/common/permission'
import { listDictItemAll } from '@/api/sys/dictItem'
import ExportExcelButton from '@/components/ExportExcelButton'
import ViewColumnsSelect from '@/views/components/ViewColumnsSelect'
import QueryForm from '@/views/components/queryForm'
import MainUI from '@/views/components/mainUI'
import OperationIcon from '@/components/OperationIcon'
import { getLocalCurrentCompany } from "@/utils/auth";
import fileurl from "@/assets/file/stuff.xlsx";


export default {
  extends: MainUI,
  components: {
    ExportExcelButton,
    ViewColumnsSelect,
    QueryForm,
    OperationIcon,
  },
  data() {
    return {
      inventory: null,
      mistake: null,
      chengGong: null,
      shiBai: null,
      dialogVisible: false,
      systemParamConfigSearch: {
        params: []
      },
      permission: {
        view: false,
        add: false,
        edit: false,
        remove: false,
        export: false
      },
      queryTypes: {
        'name': 'like',
        'type': '=',
        'bar_code': 'like',
      },
      queryModel: {
        keyword: '',
        zstbzt: '',
      },
      zstbztOptions: [
        { label: '同步', value: '1' },
        { label: '未同步', value: '2' }
      ],
      search: {
        params: [],
        offset: 0,
        limit: 20,
        columnName: '',      // 排序字段名
        order: ''            // 排序
      },
      currentPage: 1,
      stuffTotal: 0,
      stuffList: [],

      type_List: [],    // 材料类型

      oprColumnWidth: 140,  // 操作列宽
      tableId: '1004462867645374480',
      schemeId: '1004462867645374509',
      indateDialogVisible: false,  //批量设置有效期弹出框
      inventoryDialogVisible: false,   //批量设置库存预警弹出框
      importDialogVisible: false, // 批量导入弹框变量
      indate: "",   //有效期
      inventoryFloor: "",   //库存下限
      columnList: [
      {
          prop: 'ypmc',
          label: '药品名称',
        },
        {
          prop: 'pc',
          label: '生产批号',
        },
        {
          prop: 'scrq',
          label: '生产日期',
        },
        {
          prop: 'yxq',
          label: '有效期',
        },
        {
          prop: 'sl',
          label: '数量',
        },
        {
          prop: 'ckdw',
          label: '出库单位',
        },
        {
          prop: 'pfj',
          label: '批发价',
        },
        {
          prop: 'ypdm',
          label: '药品代码',
        },
        {
          prop: 'fph',
          label: '发票号',
        },
        {
          prop: 'zstbzt',
          label: '同步状态',
        },
      ],
      dispensingTime: '',
      currentData: [],
    }
  },
  methods: {
    handleSelectionChange(val) {
      this.currentData = val;
    },
    selectable(row, index) {
      if (row.zstbzt == '诊所已同步') {
        return false
      } else {
        return true
      }
    },

    indexMethod(index) {
      return (this.currentPage - 1) * this.search.limit + index + 1;
    },
    reset() {
      this.$refs.queryForm.resetFields()
      this.dispensingTime = ''
      this.onSearch()
    },
    getStuffList(val) {
      this.setLoad()
      /* 查询参数 和数据权限 */
      this.search.params = [
        { columnName: 'ypdm', queryType: '=', value: ""},
        { columnName: 'kscode', queryType: '=', value: ""},
      ]
      if (val == "1") {
        this.search.params = this.search.params.concat(this.compositeCondition())
      } else {
        // 查询参数: 药品名称
        this.search.params.push({
          columnName: 'keyword',
          queryType: 'like',
          value: this.queryModel.keyword
        })
        // 查询参数: 是否同步
        this.search.params.push({
          columnName: 'zstbzt',
          queryType: '=',
          value: this.queryModel.zstbzt
        })
        // 查询参数: 发药时间 开始日期
        this.search.params.push({
          columnName: 'fykssj',
          queryType: 'like',
          value: this.dispensingTime[0]
        })
        // 查询参数: 发药时间 结束日期
        this.search.params.push({
          columnName: 'fyjssj',
          queryType: 'like',
          value: this.dispensingTime[1]
        })
      }
      // 数据权限: 材料stuff
      this.pushDataPermissions(this.search.params, this.$route.meta.routerId, this.tableId)
      listInstitutionDispensingPage(this.search).then(responseData => {
        if (responseData.code == 100) {
          this.stuffTotal = responseData.data.total
          this.stuffList = responseData.data.rows
        } else {
          this.showMessage(responseData)
        }
        this.resetLoad()
      }).catch(error => {
        this.outputError(error)
      })
    },
    onSearch() {
      if (this.moreCodition) {
        this.search.offset = 0
        this.currentPage = 1
        this.getStuffList()
      } else {
        this.$refs['queryForm'].validate(valid => {
          if (valid) {
            this.search.offset = 0
            this.currentPage = 1
            this.getStuffList()
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
      this.getStuffList()
    },
    onCurrentChange(val) {
      this.search.offset = (val - 1) * this.search.limit
      this.currentPage = val
      this.getStuffList()
    },
    async pageInit() {
      this.setLoad()
      try {
        // this.initOptions(this.queryModel)
        // this.search.params = [{ columnName: 'company_id', queryType: '=', value: currentUser.company.id }]
        this.search.params = [
          { columnName: 'ypdm', queryType: '=', value: ""},
          { columnName: 'kscode', queryType: '=', value: ""},
        ]
        // 数据权限: 材料stuff
        this.pushDataPermissions(this.search.params, this.$route.meta.routerId, this.tableId)
        let [listStuffRespData, listPermissionRespData] = await Promise.all([
          listInstitutionDispensingPage(this.search),
          listResourcePermission(this.$route.meta.routerId)
        ])
        if (listStuffRespData.code == 100 && listPermissionRespData.code == 100) {
          this.stuffTotal = listStuffRespData.data.total
          this.stuffList = listStuffRespData.data.rows
          this.permission.view = listPermissionRespData.data.find(item => {
            return item.permission === 'hospitalDispensing:read'
          })
          this.permission.export = listPermissionRespData.data.find(item => {
            return item.permission === 'hospitalDispensing:export'
          })
          this.permission.add = listPermissionRespData.data.find(item => {
            return item.permission === 'hospitalDispensing:create'
          })
          this.permission.edit = listPermissionRespData.data.find(item => {
            return item.permission === 'hospitalDispensing:update'
          })
          this.permission.remove = listPermissionRespData.data.find(item => {
            return item.permission === 'hospitalDispensing:delete'
          })
        } else {
          this.showMessage(listPermissionRespData.code != 100 ? listPermissionRespData : listStuffRespData)
        }
        this.resetLoad()
      } catch (error) {
        this.outputError(error)
      }
    },
    onSynchronous() {
      this.$confirm('确定同步吗？', '确认', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        syncInstitutionDispensing(this.currentData).then(responseData => {
          if (responseData.code == 100) {
            this.getStuffList()
            this.showMessage({ type: 'success', msg: '同步成功' })
          } else {
            this.showMessage(responseData)
          }
        }).catch(error => {
          this.outputError(error)
        })
        
      }).catch(() => {
      })
    },
    onSortChange(orderby) {
      if (validatenull(orderby.prop)) {
        this.search.columnName = ''
        this.search.order = ''
      } else {
        this.search.columnName = orderby.prop
        this.search.order = orderby.order === 'descending' ? 'desc' : 'asc'
      }

      this.getStuffList()
    }
    ,
    initOptions(This) {
      let type_search = {
        // params: [{ 'columnName': 'dict_type_id', 'queryType': '=', 'value': '1004462867645374476' }]
      }
      // 响应字段的条件操作符，替换成触发字段的操作符
      type_search.params && type_search.params.forEach(item => {
        if (this.queryTypes[item.columnName]) {
          item.queryType = this.queryTypes[item.columnName]
        }
      })
      // 字段对应表上filter条件
      type_search.params && type_search.params.push.apply(type_search.params, [])
      // 数据权限: 字典项sys_dict_item
      this.pushDataPermissions(type_search.params, this.$route.meta.routerId, '4005')
      this.type_List.splice(0, this.type_List.length)
      listDictItemAll(type_search).then(responseData => {
        this.type_List = responseData.data
      })
    }
  },
  watch: {

    // tableData是el-table绑定的数据
    tableData: {
      // 解决表格显示错位问题
      handler() {
        this.$nextTick(() => {
          // tableRef是el-table绑定的ref属性值
          this.$refs.tableRef.doLayout()// 对 Table 进行重新布局
        })
      },
      deep: true
    }
  },
  updated() {
    if (this.$refs.tableRef) {
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
.page-container {
  padding: 0;
}

.drag_table {

  // 设置表格header的高度
  /deep/ th {
    height: 44px;
  }

  /deep/ th.gutter:last-of-type {
    height: 0 !important;
  }

  // 设置表格body的高度
  /deep/ .el-table__body-wrapper {
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
<style>
.stuff_indate .el-dialog__header {
  border-bottom: 1px solid rgb(214, 214, 214) !important;
}
</style>
<style scoped>
/deep/ .el-table__body-wrapper {
  height: calc(100% - 44px) !important;
}
</style>
