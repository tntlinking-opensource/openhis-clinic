<template>
  <div>
    <History :bussObject="curentRow"></History>
    <drug-form-new
        ref="drugFormNew"
        :permission="permission"
      />
    <el-dialog
      :visible.sync="dialogProps.visible"
      :close-on-click-modal="false"
      width="60%"
      @open="onDialogOpen()"
      v-loading="loading"
    >
      <div slot="title" class="dialog-header">
        <div style="display: flex;justify-content: space-between;">
          <span>
            {{ dialogProps.title }}
          </span>
          <div style="margin-right: 20px;">
            <span style="font-size: 14px;font-weight: 300;">来源：</span>
            <el-select
            v-model="drugSource"
            placeholder="请选择药品来源"
            @change="onDrugSourcesChange"
            >
              <el-option
                :key="1"
                label="租户"
                :value="1">
              </el-option>
              <el-option
                :key="2"
                label="院版HIS"
                :value="2">
              </el-option>
            </el-select>
            <el-divider direction="vertical"></el-divider>

          </div>

        </div>
      </div>

      <el-form
        :model="queryModel"
        label-width="70px"
        label-position="right"
        class="edit-form"
        ref="drugForm"
        :destroy-on-close="true"
      >
        <el-row  class="search-row">
          <el-col :span="8">
            <el-form-item label="药品名称" prop="goodsName">
              <el-input
                v-model="queryModel.goodsName"
                :clearable="true"
                placeholder="请输入药品名称"
              ></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="3" style="display:flex;justivy-content:space-around;">
              <el-button
                type="primary"
                icon="el-icon-search"
                @click="onSearch()"
                :plain="true"
              >搜索
              </el-button>
              <el-button
                type="info"
                icon="el-icon-refresh-left"
                @click="reset"
                :plain="true"
              >重置
              </el-button>
            </el-col>
        </el-row>
        <div class="tab-item">
          <!-- 选择的药品集合 -->
          <el-card shadow="never" v-if="selectDrugList.length" :body-style="{padding: '10px'}" style="margin-bottom: 10px;">
              <el-tag
                v-for="drug in selectDrugList"
                :key="drug.id || drug.ypId"
                closable
                @close='() => delectDrug(drug)'
                style="margin-right:10px;margin-bottom:10px;"
                >
                {{drug.goodsName}}
              </el-tag>
          </el-card>
          <el-row>
          <el-col :span="24">
          <template v-if="drugSource === 1">
            <el-table
              class="drag_table"
              :data="drugList"
              border
              row-key="id"
              height="calc(100vh - 254px)"
              :cell-class-name="cellClassName"
              :header-cell-class-name="headerCellClassName"
              highlight-current-row
              @select-all="handleSelectAll"
              @select="handleSelection"
              ref="mutipleTable">
              <el-table-column
                type="selection"
                width="40"
                :selectable="selectable">
              </el-table-column>
              <el-table-column
                label="序号"
                type="index"
                :index="indexMethod"
                align="center">
              </el-table-column>
              <el-table-column
                v-for="(cv, index) in columnViews"
                v-if="cv.display"
                :prop="cv.prop"
                :key="`columnViews_${index}`+Math.random()"
                :label="cv.label"
                :align="cv.align"
                :min-width="cv.miniWidth + 'px'"
                :width="cv.width + 'px'"
                header-align="center"
                :column-key="index.toString()"
                :render-header="renderHeader"
              >
                <template slot-scope="{ row, $index }">
                  <span
                    v-if="
                      columnViews[index].showType == 'Switch' ||
                      columnViews[index].showType == 'Checkbox' ||
                      columnViews[index].showType == 'Radio'
                    "
                  >
                    <li
                      v-if="getAttrValue(row, columnViews[index].prop) == '1'"
                      class="el-icon-check"
                      style="color: #f56c6c"
                    ></li>
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
              <el-table-column
                prop="stockNumber"
                sortable="custom"
                label="可用库存"
                width="80px"
              >
                <template slot-scope="scope">
                  <span v-if="scope.row.stock && (scope.row.stock.surplusStock != null)">
                    {{
                      Math.floor(scope.row.stock.surplusStock / scope.row.stock.surplusStock) >= 0 ?
                        Math.floor(scope.row.stock.surplusStock / scope.row.preparation) + scope.row.pack.name +
                        ((scope.row.stock.surplusStock % scope.row.preparation > 0) ? (scope.row.stock.surplusStock % scope.row.preparation) +
                          scope.row.preparationUnit.name : "") : scope.row.stock.surplusStock + scope.row.preparationUnit.name
                    }}
                  </span>
                  <span v-else>0</span>
                </template>
              </el-table-column>
              <el-table-column
                prop="stockNumber"
                sortable="custom"
                label="规格"
                width="80px"
              >
                <template slot-scope="scope">
                  <span>{{
                      `${scope.row.dosis}${scope.row.dosisUnit.name}*${scope.row.preparation}${scope.row.preparationUnit.name}/${scope.row.pack.name}`
                    }}</span>
                </template>
              </el-table-column>
            </el-table>
          </template>
          <template v-else-if="drugSource === 2">
            <el-table
              class="drag_table"
              :data="drugList"
              border
              row-key="id"
              height="calc(100vh - 254px)"
              :cell-class-name="cellClassName"
              :header-cell-class-name="headerCellClassName"
              highlight-current-row
              @select="handleSelection"
              @select-all="handleSelectAll"
              ref="mutipleTable">
              <el-table-column
                type="selection"
                width="40"
                :selectable="selectable">
              </el-table-column>
              <el-table-column
                label="序号"
                type="index"
                :index="indexMethod"
                align="center">
              </el-table-column>
              <el-table-column
                v-for="(cv, index) in versionList"
                v-if="cv.display"
                :prop="cv.prop"
                :key="`columnViews_${index}`+Math.random()"
                :label="cv.label"
                :align="cv.align"
                :min-width="cv.miniWidth + 'px'"
                :width="cv.width + 'px'"
                header-align="center"
                :column-key="index.toString()"
                :render-header="renderHeader"
              >
                <template slot-scope="{ row, $index }">
                  <template v-if="cv.prop === 'dosis'">
                    <span>{{ row.dosis }} {{ row.dosisUnit }}</span>
                  </template>
                  <template  v-else-if="cv.prop === 'preparation'">
                    <span>{{ row.preparation }} {{ row.preparationUnit }}</span>
                  </template>
                  <template  v-else-if="cv.prop === 'price'">
                    <span>{{ row.price }} 元</span>
                  </template>
                  <template  v-else-if="cv.prop === 'type'">
                    <span>{{ getTypeName(row.type) }}</span>
                  </template>
                  <template v-else-if="cv.prop === 'status'">
                    <li
                      v-if="getAttrValue(row, cv.prop) == '1'"
                      class="el-icon-check"
                      style="color: #f56c6c"
                    ></li>
                  </template>

                  <template  v-else>
                    <span>{{ getAttrValue(row, cv.prop) }}</span>
                  </template>
                </template>
              </el-table-column>
            </el-table>
          </template>
            <!-- 分页栏     开始 -->
            <el-row>
              <el-col :span="24">
                <el-pagination
                  background
                  @size-change="onSizeChange"
                  @current-change="onCurrentChange"
                  :current-page.sync="currentPage"
                  :page-sizes="[10,20, 50, 100, drugTotal]"
                  :page-size="20"
                  layout="total, sizes, prev, pager, next, jumper"
                  :total="drugTotal"
                >
                </el-pagination>
              </el-col>
            </el-row>
        </el-col>
      </el-row>

        <!-- 分页栏     结束 -->

        </div>
      </el-form>
      <span slot='footer' class='dialog-footer'>
        <el-button type='primary' :plain='true'
                   @click='onSubmit("drugForm")'>确 认</el-button>
        <el-button :plain='true' @click='onDialogClose()'>取 消</el-button>
      </span>
    </el-dialog>
  </div>
</template>
<script>
  import {validatenull} from "@/utils/validate";
  import { getDrugById, listByInstitutionPage, saveDrugSyncToClinic, listByHospitalDrug, saveHisDrugsToClinic } from "@/api/stock/drug";
  import OperationIcon from "@/components/OperationIcon";
  import MainUI from "@/views/components/mainUI";
  import ViewColumnsSelect from "@/views/components/ViewColumnsSelect";
  import DrugFormNew from "./drugForm";
  import History from "@/views/components/history";
  import { tenantList, versionList } from './metadata'

  export default {
    extends: MainUI,
    name: "drug-form",
    components: {
      OperationIcon,
      ViewColumnsSelect,
      DrugFormNew,
      History
    },
    data() {
      return {
        dialogProps: {
          visible: false,
          action: '',
          title: '',
        },
        drugSource: 1, // '药品来源'
        drugList: [],  // 租户药品列表
        currentPage: 1,
        drugTotal: 0,
        queryModel: {
          goodsName: "", // 药品名称
          type: {
            // 药品类型
            value: "",
            name: "",
          },
          barCode: "", // 条形码
          status: "1",
        },
        search: {
          params: [
            {
              columnName: "company_id",
              queryType: "=",
              value: currentUser.company.id,
            },
          ],
          offset: 0,
          limit: 20,
          columnName: "", // 排序字段名
          order: "", // 排序
        },
        selectDrugList: [],
        tableId: "",
        columnViews: tenantList,
        versionList
      };
    },
    props: {
      // 权限
      permission: {
        type: Object,
      },
    },
  methods: {
    getTypeName(type) {
      let name
      if (type == '01') {
        name = '西药'
      }
      if (type == '03') {
        name = '中药'
      }
      if (type == '0002000066') {
        name = '中成药'
      }
      return name
    },
    onSearch() {
      this.$refs["drugForm"].validate((valid) => {
        if (valid) {
          this.search.offset = 0;
          this.currentPage = 1;
          if (this.drugSource == 1) {
            this.getListByInstitution()
          } else if(this.drugSource === 2) {
            this.getlistByHospitalDrug()
          }
        } else {
          return false;
        }
      });
    },
    reset() {
      this.$refs.drugForm.resetFields()
      this.onSearch()
    },
    // 禁用已同步的药品
    selectable(row, index) {
      if (row.syncNum === 1) {
          return false;
      } else {
          return true;
      }
    },
    // 排序
    onSortChange(orderby) {
      if (validatenull(orderby.prop)) {
        this.search.columnName = "";
        this.search.order = "";
      } else {
        this.search.columnName = orderby.prop;
        if (orderby.order === null) {
          this.search.columnName = "";
          this.search.order = "";
        } else {
          this.search.order = orderby.order === "descending" ? "desc" : "asc";
        }
      }
      this.getListByInstitution();
    },
    // 药品来源发生变化
    onDrugSourcesChange(val) {
      this.selectDrugList = []
      this.drugList = []
      this.search.offset = 0;
      this.currentPage = 1;
      this.$refs.drugForm.resetFields()
      switch (val) {
        case 1:
          this.getListByInstitution()
          break;
        case 2:
          this.getlistByHospitalDrug()
          break;
        default:
          break;
      }
    },
    handleSelection(selection, row) {
      let name = this.drugSource === 1 ? 'id': 'ypId'
      let index = this.selectDrugList.findIndex(item => item[name] === row[name])
      if (index != -1) {
        this.selectDrugList.splice(index,1)
      } else {
        this.selectDrugList.push(row)
      }
    },
    handleSelectAll(selection) {
      let name = this.drugSource === 1 ? 'id': 'ypId'
      let selectDrugListCopy = [...this.selectDrugList]
      if (selection.length) {
        selection.forEach(item => {
          let index = selectDrugListCopy.findIndex(i => i[name] === item[name])
          if (index == -1) {
            selectDrugListCopy.push(item)
          }
        })
      } else {
        this.drugList.forEach(item => {
          let index = selectDrugListCopy.findIndex(i => i[name] === item[name])
          if (index != -1) {
            selectDrugListCopy.splice(index,1)
          }
        })
      }
      this.selectDrugList = selectDrugListCopy
    },
    // 删除药物
    delectDrug(row) {
      let name = this.drugSource === 1 ? 'id': 'ypId'
      let index = this.selectDrugList.findIndex(item => item[name] == row[name])
      if (index !== -1) {
        this.selectDrugList.splice(index,1)
      }
      this.handleSelectionDefault()
    },
    handleSelectionDefault() {
      this.$nextTick(() => {
        let name = this.drugSource === 1 ? 'id': 'ypId'
        if (this.selectDrugList.length) {
          this.$refs.mutipleTable.clearSelection();
          this.selectDrugList.forEach(row => {
            this.drugList.find((item) => {
              if (item[name] == row[name]) {
                this.$refs.mutipleTable.toggleRowSelection(item);
              }
            });
          })
        } else {
          this.$refs.mutipleTable.clearSelection();
        }
      })
    },

    indexMethod(index) {
      return (this.currentPage - 1) * this.search.limit + index + 1;
    },
    getlistByHospitalDrug() {
      this.setLoad();
      /* 查询参数 和数据权限 */
      this.search.params = [];
      // 查询参数: 药品名称
      this.search.params.push({
        columnName: "keyword",
        queryType: "like",
        value: this.queryModel.goodsName,
      });
      listByHospitalDrug(this.search)
        .then((responseData) => {
          if (responseData.code == 100) {
            this.drugTotal = responseData.data.total;
            this.drugList = responseData.data.rows;
            this.handleSelectionDefault()
          } else {
            this.showMessage(responseData);
          }
          this.resetLoad();
        })
        .catch((error) => {
          this.outputError(error);
        });
    },
    getListByInstitution(val) {
      this.setLoad();
      /* 查询参数 和数据权限 */
      this.search.params = [
        {
          columnName: "company_id",
          queryType: "=",
          value: currentUser.company.id,
        },
      ];
      // 查询参数: 药品名称
      this.search.params.push({
        columnName: "goods_name",
        queryType: "like",
        value: this.queryModel.goodsName,
      });

      listByInstitutionPage(this.search)
        .then((responseData) => {
          if (responseData.code == 100) {
            this.drugTotal = responseData.data.total;
            this.drugList = responseData.data.rows;
            this.handleSelectionDefault()
          } else {
            this.showMessage(responseData);
          }
          this.resetLoad();
        })
        .catch((error) => {
          this.outputError(error);
        });
    },
    onSizeChange(val) {
      this.currentPage = 1;
      this.search.limit = val;
      this.search.offset = (this.currentPage - 1) * val;
      if (this.drugSource == 1) {
        this.getListByInstitution()
      } else if(this.drugSource === 2) {
        this.getlistByHospitalDrug()
      }
    },
    onCurrentChange(val) {
      this.search.offset = (val - 1) * this.search.limit;
      this.currentPage = val;
      if (this.drugSource == 1) {
        this.getListByInstitution()
      } else if(this.drugSource === 2) {
        this.getlistByHospitalDrug()
      }
    },
    onViewDrug(index, row) {
      this.setLoad();
      getDrugById(row.id)
        .then((responseData) => {
          if (responseData.code == 100) {
            this.$refs.drugFormNew.$emit("openViewDrugDialog", responseData.data);
          } else {
            this.showMessage(responseData);
          }
          this.resetLoad();
        })
        .catch((error) => {
          this.outputError(error);
        });
    },
    OpenEdit(index, row) {
      this.setLoad();
      getDrugById(row.id)
        .then((responseData) => {
          if (responseData.code == 100) {
            this.$refs.drugFormNew.$emit("openEditDrugDialog", responseData.data);
          } else {
            this.showMessage(responseData);
          }
          this.resetLoad();
        })
        .catch((error) => {
          this.outputError(error);
        });
    },
    onSubmit(formName) {
      if (this.drugSource === 1) {
        saveDrugSyncToClinic(this.selectDrugList)
          .then((responseData) => {
            if (responseData.code == 100) {
              this.dialogProps.visible = false
              this.$emit('sync-finished')
            }
          })
          .catch((error) => {
            this.outputError(error);
          });
      } else if (this.drugSource === 2) {
        saveHisDrugsToClinic(this.selectDrugList)
          .then((responseData) => {
            if (responseData.code == 100) {
              this.dialogProps.visible = false
              this.$emit('sync-finished')
            }
          })
          .catch((error) => {
            this.outputError(error);
          });
      }
    },
    onDialogClose() {
      this.dialogProps.visible = false;
    },
    onDialogOpen() {
      this.$nextTick(() => {
        this.$refs["drugForm"].clearValidate();
      });
    },
  },
  watch: {
    drugList(val) {
      if (val) {
        this.$nextTick(() => {
          this.$refs.mutipleTable && this.$refs.mutipleTable.doLayout();
        });
      }
    },
    tableData: {
        // 解决表格显示错位问题
        handler() {
          this.$nextTick(() => {
            // mutipleTable是el-table绑定的ref属性值
            this.$refs.mutipleTable.doLayout()// 对 Table 进行重新布局
          })
        },
        deep: true
      }
  },
  mounted: function () {
    this.$nextTick(() => {
      this.$on("openMigratingDrugDialog", function (data) {
          this.drugSource = 1
          this.dialogProps.action = "add";
          this.dialogProps.title = "同步药品信息";
          this.search.offset = 0;
          this.currentPage = 1;
          this.drugList = []
          this.selectDrugList = []
          if (this.drugSource === 1) {
            this.getListByInstitution()
          } else if (this.drugSource === 2) {
            this.getlistByHospitalDrug()
          }
          this.dialogProps.visible = true;
        });
      });
    },
  };
</script>
<style scoped>
  /deep/ .el-dialog__body {
    padding: 30px 20px 0;
  }

  /deep/ .info .el-form-item__label {
    width: 80px !important;
  }

  /deep/ .info .el-form-item__content {
    margin-left: 80px !important;
  }
  /deep/ .el-dialog__headerbtn {
    top: 24px;
  }
  .edit-form {
    margin-top: -30px;
  }
</style>
<style scoped lang="scss">
  .specifications {
    width: 100%;
    font-weight: bold;
    padding-bottom: 8px;

    padding: 20px;
    color: #333;
  }

  /deep/.title-wrap {
    display: flex;
    align-items: center;
    justify-content: space-between;
    .el-form-item--mini.el-form-item {
      margin-bottom: 0px;
    }
  }


</style>
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

  .indate /deep/ .el-dialog__header {
    border-bottom: 1px solid rgb(214, 214, 214) !important;
  }
</style>
