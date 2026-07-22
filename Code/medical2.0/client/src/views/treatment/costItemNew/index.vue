<template>
  <el-row v-loading="loading">
    <!-- 历史记录  -->
    <History :bussObject="curentRow"></History>
    <!-- 编辑窗口  -->
    <costItem-form ref="costItemForm" :permission="permission" v-on:save-finished="loadData"></costItem-form>
    <!-- 同步 -->
    <syncCostItemForm ref="synchronousRef" v-on:save-finished="loadData"></syncCostItemForm>
    <el-card class="page-container">
      <!--  搜索栏  开始 -->
      <div class="query-form-container">
        <el-row v-if="!moreCodition" class="search-row">
          <el-form :model="queryModel" @submit.native.prevent label-position="left" label-width="70px" ref="queryForm"
            :inline-message="true">
            <el-col :span="6">
              <el-form-item label="项目名称" prop="itemName">
                <el-input v-model="queryModel.itemName" :clearable="true" placeholder="请输入项目名称"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item label="项目类别" prop="itemType">
                <el-select v-model="queryModel.itemType" value-key="value" filterable clearable placeholder="请选择项目类别">
                  <el-option v-for="itemType in itemType_List" :key="itemType.value" :label="itemType.name"
                    :value="itemType"></el-option>
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="5">
              <el-form-item label='启用状态' prop='isUse'>
                <el-select v-model="queryModel.isUse" placeholder="请选择是否启用">
                  <el-option v-for="item in isUse" :key="item.value" :label="item.label" :value="item.value">
                  </el-option>
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="3" style="display:flex;justivy-content:space-around">
              <el-button type="primary" icon="el-icon-search" @click="onSearch()" :plain="true">搜索</el-button>
              <el-button type="info" icon="el-icon-refresh-left" @click="reset" :plain="true">重置</el-button>
            </el-col>
            <el-col :span="4" style="text-align:right;padding-right:5px">
              <el-button-group>
                <el-button style="margin-right: 10px;" type="primary" icon="el-icon-refresh" @click="onSynchronous()">同步
                </el-button>
                <el-button v-show="permission.add" type="primary" icon="el-icon-plus"
                  @click="onCreateCostItem()">添加</el-button>
              </el-button-group>
            </el-col>
          </el-form>
        </el-row>
        <QueryForm v-else v-model="moreParm" :tableId="tableId" :schemeId="schemeId" :routerId="$route.meta.routerId"
          @search="onSearch()" @moreCodition="onMoreCodition()"></QueryForm>
      </div>
      <!--  搜索栏  结束 -->

      <!-- 工具栏 开始 -->

      <!-- 工具栏 结束 -->

      <!-- 表格栏  开始 -->
      <el-row>
        <el-col :span="24">
          <div @mouseleave="moveTableOutside">
            <el-table class="drag_table" :data="costItemList" border :height="tableHeight" @sort-change="onSortChange"
              @header-dragend="onChangeWidth" :cell-class-name="cellClassName"
              :header-cell-class-name="headerCellClassName" highlight-current-row ref="tableRef">
              <el-table-column label="序号" type="index" :index="indexMethod" align="center">
              </el-table-column>
              <el-table-column v-for="(cv, index) in columnViews" v-if="cv.display" :prop="cv.prop"
                :key="`columnViews_${index}`" :label="cv.label" sortable="custom" :align="cv.align"
                :min-width="cv.miniWidth + 'px'" :width="cv.width + 'px'" header-align="center"
                :column-key="index.toString()" :render-header="renderHeader">
                <template slot-scope="{ row, $index }">
                  <span v-if="columnViews[index].showType === 'Switch' ||
    columnViews[index].showType === 'Checkbox' ||
    columnViews[index].showType === 'Radio'
    ">
                    <li v-if="getAttrValue(row, columnViews[index].prop) === '1'" class="el-icon-check"
                      style="color: #f56c6c"></li>
                  </span>
                  <span v-else>{{
    getAttrValue(
      row,
      columnViews[index].prop,
      columnViews[index].javaType
    )
  }}</span>
                </template>
              </el-table-column>
              <!--表行级操作按钮-->
              <el-table-column label="操作" header-align="center" :width="130 + 'px'" :key="Math.random()">
                <template slot="header" slot-scope="scope">
                  <span>操作</span>
                  <view-columns-select v-model="columnViews" v-on:save-column-view="saveColumn"
                    v-on:show-all-column="showAllColumn"
                    v-on:show-default-column="showDefaultColumn"></view-columns-select>
                  <export-excel-button v-show="permission.export" :data="costItemList" :tHeader="getHeads()"
                    :filterVal="getFilterVal()" :plain="true"></export-excel-button>
                </template>
                <template slot-scope="scope">
                  <OperationIcon v-show="permission.view" type="info" content="查看" placement="top-start"
                    icon-name="el-icon-view" @click="onViewCostItem(scope.$index, scope.row)"></OperationIcon>
                  <OperationIcon v-show="permission.edit" type="primary" content="编辑" placement="top-start"
                    icon-name="el-icon-edit" @click="onEditCostItem(scope.$index, scope.row)"></OperationIcon>
                  <OperationIcon v-show="permission.add" type="primary" content="复制" placement="top-start"
                    icon-name="el-icon-document" @click="onCopyCostItem(scope.$index, scope.row)"></OperationIcon>
                  <OperationIcon v-show="permission.view" type="info" content="历史记录" placement="top-start"
                    icon-name="el-icon-info" @click="onShowHistory(scope.$index, scope.row)"></OperationIcon>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </el-col>
      </el-row>
      <!-- 表格栏  结束 -->
      <!-- 分页栏     开始 -->
      <el-row>
        <el-col :span="24">
          <el-pagination background @size-change="onSizeChange" @current-change="onCurrentChange"
            :current-page.sync="currentPage" :page-sizes="[20, 50, 100, costItemTotal]" :page-size="20"
            layout="total, sizes, prev, pager, next, jumper" :total="costItemTotal">
          </el-pagination>
        </el-col>
      </el-row>
      <!-- 分页栏     结束 -->
    </el-card>
  </el-row>
</template>

<script>
import {
  listCostItemPage,
  getCostItemById,
  listCostItemAll,
} from "@/api/treatment/costItem";
import listViewMixin from "@/mixins/listViewMixin";
import CostItemForm from "./costItemForm";
import { listDictItemAll } from "@/api/sys/dictItem";
import { getDictItemsByCode, DICT_CODE } from '@/utils/dictCache'
import ExportExcelButton from "@/components/ExportExcelButton";
import ViewColumnsSelect from "@/views/components/ViewColumnsSelect";
import QueryForm from "@/views/components/queryForm";
import MainUI from "@/views/components/mainUI";
import OperationIcon from "@/components/OperationIcon";
import History from "@/views/components/history";
import syncCostItemForm from "./syncCostItemForm.vue";
export default {
  extends: MainUI,
  mixins: [listViewMixin],
  components: {
    CostItemForm,
    ExportExcelButton,
    ViewColumnsSelect,
    QueryForm,
    OperationIcon,
    History,
    syncCostItemForm
  },

  data() {
    return {
      listApi: listCostItemPage,
      getApi: getCostItemById,
      entityName: 'CostItem',
      permissionPrefix: 'costItemNew',
      costItemDTO: {
        response: {},
        res: {}
      },
      queryTypes: {
        item_name: "like",
        item_type: "=",
        is_use: "=",
      },
      queryModel: {
        itemName: "", // 项目名称
        itemType: {
          // 项目类别
          value: "",
          name: "",
        },
        isUse: "", // 状态
      },
      tableHeight: "", //表格高度
      isUse: [
        {
          value: '0',
          label: '否'
        },
        {
          value: '1',
          label: '是'
        },
      ],
      costItemTotal: 0,
      costItemList: [],

      itemType_List: [], // 项目类别

      oprColumnWidth: 140, // 操作列宽
      tableId: "998465736089977637",
      schemeId: "998465736089977654",
    };
  },
  methods: {
    appendSearchParams() {
      // 查询参数: 项目名称
      this.search.params.push({
        columnName: "item_name",
        queryType: "like",
        value: this.queryModel.itemName,
      });
      if (this.queryModel.itemType.value !== "") {
        // 查询参数: 项目类别
        this.search.params.push({
          columnName: "item_type",
          queryType: "=",
          value: this.queryModel.itemType.value || "",
        });
      }
      // 查询参数: 状态
      this.search.params.push({
        columnName: "is_use",
        queryType: "=",
        value: this.queryModel.isUse,
      });
      // 数据权限: 费用项目cost_item
      this.pushDataPermissions(
        this.search.params,
        this.$route.meta.routerId,
        this.tableId
      );
    },
    handleListResponse(responseData) {
      this.costItemTotal = responseData.data.total;
      this.costItemList = responseData.data.rows;
    },
    initOptions(This) {
      getDictItemsByCode(DICT_CODE.TREATMENT_ITEM_TYPE).then((data) => {
        this.itemType_List = data;
      });
    },
    //计算table高度(动态设置table高度)
    getTableHeight() {
      let tableH = 240;
      let tableHeightDetil = window.innerHeight - tableH;
      if (tableHeightDetil <= 300) {
        this.tableHeight = 300;
      } else {
        this.tableHeight = window.innerHeight - tableH;
      }
    },
    onSynchronous() {
      this.$refs.synchronousRef.$emit('openSyncCostItemDialog')
    },
    onCreateCostItem() {
      this.search.params = [
        {
          columnName: "company_id",
          queryType: "=",
          value: (function () {
            const user = JSON.parse(sessionStorage.getItem("currentUser"));
            return user.company.id;
          })(),
        },
      ];
      listCostItemAll(this.search).then((res) => {
        if (res.code === 100) {
          this.$refs.costItemForm.$emit("openAddCostItemDialog", res.data);
        }
      }).catch(() => { })
    },
    onViewCostItem(index, row) {
      this.setLoad();
      getCostItemById(row.id)
        .then((responseData) => {
          if (responseData.code === 100) {
            this.$refs.costItemForm.$emit("openViewCostItemDialog", responseData.data);
          } else {
            this.showMessage(responseData);
          }
          this.resetLoad();
        })
        .catch((error) => {
          this.outputError(error);
        });
    },
    onEditCostItem(index, row) {
      this.setLoad();
      getCostItemById(row.id)
        .then((responseData) => {
          if (responseData.code === 100) {
            this.search.params = [
              {
                columnName: "company_id",
                queryType: "=",
                value: (function () {
                  const user = JSON.parse(sessionStorage.getItem("currentUser"));
                  return user.company.id;
                })(),
              },
            ];
            listCostItemAll(this.search).then((res) => {
              if (res.code === 100) {
                this.costItemDTO.response = responseData.data
                this.costItemDTO.res = res.data
                this.$refs.costItemForm.$emit("openEditCostItemDialog", this.costItemDTO);
              }
            }).catch(() => { })
          } else {
            this.showMessage(responseData);
          }
          this.resetLoad();
        })
        .catch((error) => {
          this.outputError(error);
        });
    },
    onCopyCostItem(index, row) {
      this.setLoad();
      getCostItemById(row.id)
        .then((responseData) => {
          if (responseData.code === 100) {
            this.search.params = [
              {
                columnName: "company_id",
                queryType: "=",
                value: (function () {
                  const user = JSON.parse(sessionStorage.getItem("currentUser"));
                  return user.company.id;
                })(),
              },
            ];
            listCostItemAll(this.search).then((res) => {
              if (res.code === 100) {
                this.costItemDTO.response = responseData.data
                this.costItemDTO.res = res.data
                this.$refs.costItemForm.$emit("openCopyCostItemDialog", this.costItemDTO);
              }
            }).catch(() => { })
          } else {
            this.showMessage(responseData);
          }
          this.resetLoad();
        })
        .catch((error) => {
          this.outputError(error);
        });
    },
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
    this.pageInit();
    //挂载window.onresize事件(动态设置table高度)
    this._resizeHandler = () => {
      if (this.resizeFlag) {
        clearTimeout(this.resizeFlag);
      }
      this.resizeFlag = setTimeout(() => {
        this.getTableHeight();
        this.resizeFlag = null;
      }, 100);
    };
    window.addEventListener('resize', this._resizeHandler);
  },
  beforeDestroy() {
    window.removeEventListener('resize', this._resizeHandler);
    if (this.resizeFlag) {
      clearTimeout(this.resizeFlag);
    }
  },
  created() {
    this.getTableHeight();
  }
};
</script>
<style lang="scss" scoped>
.page-container {
  padding: 0;
}

.drag_table {

  // 设置表格header的高度
  ::v-deep th {
    height: 44px;
  }

  ::v-deep th.gutter:last-of-type {
    height: 0 !important;
  }

  // 设置表格body的高度
  ::v-deep.el-table__body-wrapper {
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
<style scoped>
::v-deep .el-table__body-wrapper {
  height: calc(100% - 44px) !important;
}
</style>
