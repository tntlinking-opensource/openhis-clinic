<template>
  <el-dialog :title="dialogProps.title" :visible.sync="dialogProps.visible" :close-on-click-modal="false" width="90%"
    v-loading="loading" :destroy-on-close="true">
    <el-row v-loading="loading">
      <costItem-form ref="costItemForm" :permission="permission" v-on:save-finished="pageInit"></costItem-form>
      <el-card class="page-container">
        <div class="query-form-container">
          <el-row v-if="!moreCodition" class="search-row">
            <el-form :model="queryModel" @submit.native.prevent label-position="left" label-width="70px" ref="queryForm"
              :inline-message="true">
              <el-col :span="6">
                <el-form-item label="项目名称" prop="itemName">
                  <el-input v-model="queryModel.itemName" :clearable="true" placeholder="请输入项目名称"></el-input>
                </el-form-item>
              </el-col>
              <el-col :span="3" style="display:flex;justivy-content:space-around">
                <el-button type="primary" icon="el-icon-search" @click="onSearch()" :plain="true">搜索</el-button>
                <el-button type="info" icon="el-icon-refresh-left" @click="reset" :plain="true">重置</el-button>
              </el-col>
              <el-col :span="4" style="text-align:right;padding-right:5px">
                <!-- <el-button-group>
                  <el-button v-show="permission.add" type="primary" icon="el-icon-plus"
                    @click="onCreateCostItem()">添加</el-button>
                </el-button-group> -->
              </el-col>
            </el-form>
          </el-row>
          <QueryForm v-else v-model="moreParm" :tableId="tableId" :schemeId="schemeId" :routerId="$route.meta.routerId"
            @search="onSearch()" @moreCodition="onMoreCodition()"></QueryForm>
        </div>

        <el-row>
          <el-col :span="24">
            <div style="display: flex;align-items: center;">
              <span style="width: 100%;">已选择{{ multipleSelection.length }}条</span>
            <div style="display: flex;margin-bottom: 10px;width: 100%;justify-content: flex-end;align-items: center;">
              <div style="width: 80px;">数据来源</div>
              <el-select @change="handleSource" v-model="source">
                <el-option label="租户" value="1"></el-option>
                <el-option label="院版His" value="2"></el-option>
              </el-select>
            </div>
            </div>
            <div @mouseleave="moveTableOutside">
              <el-table class="drag_table" :data="costItemList" :row-key="getRowKey" border :height="tableHeight" @sort-change="onSortChange"
                @header-dragend="onChangeWidth" :cell-class-name="cellClassName"
                :header-cell-class-name="headerCellClassName" highlight-current-row ref="tableRef"
                @selection-change="handleSelectionChange">
                <el-table-column type='selection' :reserve-selection="true" width='50' align='center' :selectable="selectable">
                </el-table-column>
                <el-table-column label="序号" type="index" :index="indexMethod" align="center">
                </el-table-column>
                <el-table-column v-for="item in columnList" :label="item.label" :prop="item.prop"></el-table-column>
                <!-- <el-table-column label="操作" header-align="center" :width="130 + 'px'" fixed="right"
                  :key="Math.random()">
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
                </el-table-column> -->
              </el-table>
            </div>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-pagination background @size-change="onSizeChange" @current-change="onCurrentChange"
              :current-page.sync="currentPage" :page-sizes="[20, 50, 100, costItemTotal]" :page-size="20"
              layout="total, sizes, prev, pager, next, jumper" :total="costItemTotal">
            </el-pagination>
          </el-col>
        </el-row>
      </el-card>
    </el-row>
    <span slot='footer' class='dialog-footer'>
      <el-button :disabled="flage" type='primary' :plain='true' @click='onSubmit()'>同 步</el-button>
      <el-button v-if='dialogProps.action != "view"' :plain='true' @click='onDialogClose()'>取 消</el-button>
    </span>
  </el-dialog>
</template>
<script>
import { validatenull } from "@/utils/validate";
import {
  listCostItemPageByParent,
  getCostItemById,
  deleteCostItem,
  listCostItemPageByInstitution,
  listCostItemPageByInstitutionAll,
  listCostItemPageByInstitutionSync
} from "@/api/treatment/costItem";
import { listResourcePermission } from "@/api/admin/common/permission";
import CostItemForm from "./costItemForm";
import { listDictItemAll } from "@/api/sys/dictItem";
import { listCostItemAll } from "@/api/treatment/costItem";
import ExportExcelButton from "@/components/ExportExcelButton";
import ViewColumnsSelect from "@/views/components/ViewColumnsSelect";
import QueryForm from "@/views/components/queryForm";
import MainUI from "@/views/components/mainUI";
import OperationIcon from "@/components/OperationIcon";
import History from "@/views/components/history";


export default {
  extends: MainUI,
  name: "stuff-form",
  components: {
    ExportExcelButton,
    ViewColumnsSelect,
    QueryForm,
    OperationIcon,
    History,
    CostItemForm
  },
  data() {
    return {
      dialogProps: {
        visible: false,
        action: '',
        title: '',
      },
      permission: {
        view: false,
        add: false,
        edit: false,
        remove: false,
        export: false,
      },
      costItemDTO: {
        response: {},
        res: {}
      },//修改操作对象封装
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
      tableHeight: "",//表格高度
      isUse: [
        {
          value: '0',
          label: '否'
        },
        {
          value: '1',
          label: '是'
        },
      ],//是否启用
      search: {
        params: [
          {
            columnName: "company_id",
            queryType: "=",
            value: (function () {
              var user = JSON.parse(sessionStorage.getItem("currentUser"));
              return user.company.id;
            })(),
          },
        ],
        offset: 0,
        limit: 20,
        columnName: "", // 排序字段名
        order: "", // 排序
      },
      subproject: [],//子项目封装
      costltemSearch: {
        params: [{ columnName: "company_id", queryType: "=", value: "" }],
        offset: 0,
        limit: 100,
        columnName: "", // 排序字段名
        order: "", // 排序
      },
      currentPage: 1,
      costItemTotal: 0,
      costItemList: [],

      itemType_List: [], // 项目类别

      oprColumnWidth: 140, // 操作列宽
      tableId: "998465736089977637",
      schemeId: "998465736089977654",
      multipleSelection: [],
      source: '1',
      columnList: [],
      columnListOne: [
        {
          label: "项目编码",
          prop: "itemCode",
        },
        {
          label: "项目名称",
          prop: "itemName",
        },
        {
          label: "项目类别",
          prop: "itemTypeName",
        },
        {
          label: "成本价",
          prop: "costPrice",
        },
        {
          label: "销售价",
          prop: "salePrice",
        },
        {
          label: "启用状态",
          prop: "isUseName",
        },
        {
          prop: 'syncNumName',
          label: '是否已同步',
        },
        {
          label: "备注信息",
          prop: "remarks",
        },
      ],
      columnListTwo: [
        {
          label: "项目名称",
          prop: "sfxmmc",
        },
        {
          label: "拼音",
          prop: "py",
        },
        {
          label: "单位",
          prop: "dw",
        },
        {
          label: "项目编码",
          prop: "sfxmCode",
        },
        {
          label: "销售单价",
          prop: "dj",
        },
        {
          label: "收费项目类型",
          prop: "dllx",
        },
        {
          prop: 'syncNumName',
          label: '是否已同步',
        },
      ]
    };
  },
  methods: {
    getRowKey(row) {
      return row.sfxmId || row.id;
    },
    handleSource(val) {
      this.source = val
      if (this.source == 1) {
        this.columnList = this.columnListOne
      } else {
        this.columnList = this.columnListTwo
      }
      this.search.offset = 0
      this.currentPage = 1
      this.$refs.queryForm.resetFields()
      this.pageInit()
    },
    selectable(row, index) {
      if (row.syncNum == 1) {
        return false
      } else {
        return true
      }
    },
    async onSubmit() {
      if (this.multipleSelection.length == 0) {
        this.$message({
          message: "请选择要同步的项目",
          type: "warning",
        });
        return;
      }
      const functionSync = this.source == 1 ? listCostItemPageByInstitution : listCostItemPageByInstitutionSync
      const result = await functionSync(this.multipleSelection)
      if (result.code == 100) {
        this.$message({
          message: "同步成功",
          type: "success",
        });
        this.onDialogClose()
        this.$emit("save-finished");
      } else {
        this.$message({
          message: result.msg,
          type: "error",
        });
      }
    },
    onDialogClose() {
      this.dialogProps.visible = false;
    },
    handleSelectionChange(val) {
      this.multipleSelection = val;
    },
    //计算table高度(动态设置table高度)
    getTableHeight() {
      let tableH = 240; //距离页面下方的高度
      let tableHeightDetil = window.innerHeight - tableH;
      if (tableHeightDetil <= 300) {
        this.tableHeight = 300;
      } else {
        this.tableHeight = window.innerHeight - tableH;
      }
    },


    indexMethod(index) {
      return (this.currentPage - 1) * this.search.limit + index + 1;
    },
    reset() {
      this.$refs.queryForm.resetFields()
      this.onSearch()
    },
    getCostItemList() {
      this.setLoad();
      /* 查询参数 和数据权限 */
      this.search.params = [
        {
          columnName: "company_id",
          queryType: "=",
          value: (function () {
            var user = JSON.parse(sessionStorage.getItem("currentUser"));
            return user.company.id;
          })(),
        },
      ];
      if (this.moreCodition) {
        console.log(this.moreCodition, "---=====");
        this.search.params = this.search.params.concat(
          this.compositeCondition()
        );
      } else {
        console.log(this.search, "---+++++");
        // 查询参数: 项目名称
        this.search.params.push({
          columnName: this.source == 1 ? "item_name" : 'keyword',
          queryType: "like",
          value: this.queryModel.itemName,
        });
        if (this.queryModel.itemType.value != "") {
          // 查询参数: 项目类别
          this.search.params.push({
            columnName: "item_type",
            queryType: "=",
            value: validatenull(this.queryModel.itemType.value)
              ? ""
              : this.queryModel.itemType.value,
          });
        }

        // 查询参数: 状态
        this.search.params.push({
          columnName: "is_use",
          queryType: "=",
          value: this.queryModel.isUse,
        });
      }
      // 数据权限: 费用项目cost_item
      this.pushDataPermissions(
        this.search.params,
        this.$route.meta.routerId,
        this.tableId
      );
      const functionList = this.source == 1 ? listCostItemPageByParent : listCostItemPageByInstitutionAll
      functionList(this.search)
        .then((responseData) => {
          if (responseData.code == 100) {
            if (this.source == 1) {
            this.costItemList = responseData.data.rows && responseData.data.rows.map(item => {
              item.itemTypeName = item.itemType.name
              item.syncNumName = item.syncNum == 1 ? '是' : '否'
              item.isUseName = isUse == 1 ? '是' : '否'
              return item
            })
          } else {
            this.costItemList = responseData.data.rows && responseData.data.rows.map(item => {
              item.syncNumName = item.syncNum == 1 ? '是' : '否'
              return item
            })
          }
            this.costItemTotal = responseData.data.total;
          } else {
            this.showMessage(responseData);
          }
          this.resetLoad();
        })
        .catch((error) => {
          this.outputError(error);
        });
    },
    onSearch() {
      if (this.moreCodition) {
        this.search.offset = 0;
        this.currentPage = 1;
        this.getCostItemList();
      } else {
        this.$refs["queryForm"].validate((valid) => {
          if (valid) {
            this.search.offset = 0;
            this.currentPage = 1;
            this.getCostItemList();
          } else {
            return false;
          }
        });
      }
    },
    onSizeChange(val) {
      this.currentPage = 1;
      this.search.limit = val;
      this.search.offset = (this.currentPage - 1) * val;
      this.getCostItemList();
    },
    onCurrentChange(val) {
      this.search.offset = (val - 1) * this.search.limit;
      this.currentPage = val;
      this.getCostItemList();
    },
    async pageInit() {

      this.setLoad();
      try {
        this.initOptions(this.queryModel);
        this.search.params = [
          {
            columnName: "company_id",
            queryType: "=",
            value: (function () {
              var user = JSON.parse(sessionStorage.getItem("currentUser"));
              return user.company.id;
            })(),
          },
        ];
        // 数据权限: 费用项目cost_item
        this.pushDataPermissions(
          this.search.params,
          this.$route.meta.routerId,
          this.tableId
        );
        const functionList = this.source == 1 ? listCostItemPageByParent : listCostItemPageByInstitutionAll
        let [listCostItemRespData, listPermissionRespData] = await Promise.all([
          functionList(this.search),
          listResourcePermission(this.$route.meta.routerId),
        ]);
        if (
          listCostItemRespData.code == 100 &&
          listPermissionRespData.code == 100
        ) {
          this.costItemTotal = listCostItemRespData.data.total;
          if (this.source == 1) {
            this.costItemList = listCostItemRespData.data.rows && listCostItemRespData.data.rows.map(item => {
              item.itemTypeName = item.itemType.name
              item.syncNumName = item.syncNum == 1 ? '是' : '否'
              item.isUseName = isUse == 1 ? '是' : '否'
              return item
            })
          } else {
            this.costItemList = listCostItemRespData.data.rows && listCostItemRespData.data.rows.map(item => {
              item.syncNumName = item.syncNum == 1 ? '是' : '否'
              return item
            })
          }

          console.log(this.costItemList, '--')
          this.permission.view = listPermissionRespData.data.find((item) => {
            return item.permission === "costItemNew:read";
          });
          this.permission.export = listPermissionRespData.data.find((item) => {
            return item.permission === "costItemNew:export";
          });
          this.permission.add = listPermissionRespData.data.find((item) => {
            return item.permission === "costItemNew:create";
          });
          this.permission.edit = listPermissionRespData.data.find((item) => {
            return item.permission === "costItemNew:update";
          });
          this.permission.remove = listPermissionRespData.data.find((item) => {
            return item.permission === "costItemNew:delete";
          });
        } else {
          this.showMessage(
            listPermissionRespData.code != 100
              ? listPermissionRespData
              : listCostItemRespData
          );
        }
        this.resetLoad();
      } catch (error) {
        this.outputError(error);
      }
    },
    onViewCostItem(index, row) {
      this.setLoad();
      getCostItemById(row.id)
        .then((responseData) => {
          console.log(responseData);
          if (responseData.code == 100) {
            console.log(responseData.data);
            this.$refs.costItemForm.$emit(
              "openViewCostItemDialog",
              responseData.data
            );
          } else {
            this.showMessage(responseData);
          }
          this.resetLoad();
        })
        .catch((error) => {
          this.outputError(error);
        });
    },
    onCreateCostItem() {
      //  this.costltemSearch.params = [
      //   {
      //     columnName: "company_id",
      //     queryType: "=",
      //     value: this.company_id,
      //   },
      // ];
      this.search.params = [
        {
          columnName: "company_id",
          queryType: "=",
          value: (function () {
            var user = JSON.parse(sessionStorage.getItem("currentUser"));
            return user.company.id;
          })(),
        },
      ];
      listCostItemAll(this.search).then((res) => {
        if (res.code == 100) {
          // this.subproject=res.data
          this.$refs.costItemForm.$emit("openAddCostItemDialog", res.data);
        }
      }).catch(() => { })

    },
    onEditCostItem(index, row) {
      this.setLoad();
      getCostItemById(row.id)
        .then((responseData) => {
          if (responseData.code == 100) {

            this.search.params = [
              {
                columnName: "company_id",
                queryType: "=",
                value: (function () {
                  var user = JSON.parse(sessionStorage.getItem("currentUser"));
                  return user.company.id;
                })(),
              },
            ];
            listCostItemAll(this.search).then((res) => {
              if (res.code == 100) {
                // this.subproject=res.data
                this.costItemDTO.response = responseData.data
                this.costItemDTO.res = res.data

                this.$refs.costItemForm.$emit(
                  "openEditCostItemDialog",
                  this.costItemDTO
                );
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
      // getCostItemById(row.id)
      //   .then((responseData) => {
      //     if (responseData.code == 100) {
      //       this.$refs.costItemForm.$emit(
      //         "openCopyCostItemDialog",
      //         responseData.data
      //       );
      //     } else {
      //       this.showMessage(responseData);
      //     }
      //     this.resetLoad();
      //   })
      //   .catch((error) => {
      //     this.outputError(error);
      //   });
      getCostItemById(row.id)
        .then((responseData) => {
          if (responseData.code == 100) {

            this.search.params = [
              {
                columnName: "company_id",
                queryType: "=",
                value: (function () {
                  var user = JSON.parse(sessionStorage.getItem("currentUser"));
                  return user.company.id;
                })(),
              },
            ];
            listCostItemAll(this.search).then((res) => {
              if (res.code == 100) {
                // this.subproject=res.data
                this.costItemDTO.response = responseData.data
                this.costItemDTO.res = res.data

                this.$refs.costItemForm.$emit(
                  "openCopyCostItemDialog",
                  this.costItemDTO
                );
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
    onDeleteCostItem(index, row) {
      this.$confirm("确定删除吗？", "确认", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      })
        .then(() => {
          this.setLoad();
          deleteCostItem(row)
            .then((responseData) => {
              if (responseData.code == 100) {
                this.getCostItemList();
                this.showMessage({ type: "success", msg: "删除成功" });
              } else {
                this.showMessage(responseData);
              }
              this.resetLoad();
            })
            .catch((error) => {
              this.outputError(error);
            });
        })
        .catch(() => { });
    },
    onSortChange(orderby) {
      if (validatenull(orderby.prop)) {
        this.search.columnName = "";
        this.search.order = "";
      } else {
        this.search.columnName = orderby.prop;
        this.search.order = orderby.order === "descending" ? "desc" : "asc";
      }

      this.getCostItemList();
    },
    initOptions(This) {
      let itemType_search = {
        params: [
          {
            columnName: "dict_type_id",
            queryType: "=",
            value: "998465736089977631",
          },
        ],
      };
      // 响应字段的条件操作符，替换成触发字段的操作符
      itemType_search.params.forEach((item) => {
        if (this.queryTypes[item.columnName]) {
          item.queryType = this.queryTypes[item.columnName];
        }
      });
      // 字段对应表上filter条件
      itemType_search.params.push.apply(itemType_search.params, []);
      // 数据权限: 字典项sys_dict_item
      this.pushDataPermissions(
        itemType_search.params,
        this.$route.meta.routerId,
        "4005"
      );
      this.itemType_List.splice(0, this.itemType_List.length);
      listDictItemAll(itemType_search).then((responseData) => {
        this.itemType_List = responseData.data;
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
        this.$refs.tableRef.doLayout()// 对 Table 进行重新布局
      })
    }
  },
  mounted() {
    if (this.source == 1) {
      this.columnList = this.columnListOne
    } else {
      this.columnList = this.columnListTwo
    }
    this.$nextTick(() => {
      this.$on("openSyncCostItemDialog", () => {
        this.dialogProps.title = "同步诊疗项目";
        this.dialogProps.visible = true;
        this.pageInit()
      });
    });
    let _this = this;
    window.onresize = () => {
      if (_this.resizeFlag) {
        clearTimeout(_this.resizeFlag);
      }
      _this.resizeFlag = setTimeout(() => {
        _this.getTableHeight();
        _this.resizeFlag = null;
      }, 100);
    };
  },
  created() {
    this.getTableHeight();
  }
};
</script>
<style scoped lang="scss">
.line {
  margin-top: -10px;
}

.specifications {
  margin-top: -30px;
  width: 100%;
  font-weight: bold;
  padding-bottom: 8px;
  padding: 20px;
  color: #333;
}

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
