<template>
  <el-row v-loading="loading">
    <!-- 历史记录  -->
    <History :bussObject="curentRow"></History>
    <!-- 编辑窗口  -->
    <drug-form
      ref="drugForm"
      :permission="permission"
      v-on:save-finished="getDrugList"
    ></drug-form>
    <DrugListSynchronization
      ref="drugListSynchronization"
      :permission="permission"
      v-on:sync-finished="handleSync"
    />
    <el-card class="page-container">
      <!--  搜索栏  开始 -->
      <div class="query-form-container">
        <el-row v-if="!moreCodition" class="search-row">
          <el-form
            :model="queryModel"
            @submit.native.prevent
            label-position="left"
            label-width="70px"
            ref="queryForm"
            :inline-message="true"
          >
            <el-col :span="4">
              <el-form-item label="药品名称" prop="goodsName">
                <el-input
                  v-model="queryModel.goodsName"
                  :clearable="true"
                  placeholder="请输入药品名称"
                ></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="4">
              <el-form-item label="药品类型" prop="type">
                <el-select
                  v-model="queryModel.type"
                  value-key="value"
                  filterable
                  clearable
                  placeholder="请选择药品类型"
                >
                  <el-option
                    v-for="item in type_List"
                    :key="item.value"
                    :label="item.name"
                    :value="item"
                  ></el-option>
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="4">
              <el-form-item label="条形码" prop="barCode">
                <el-input
                  v-model="queryModel.barCode"
                  :clearable="true"
                  placeholder="请输入条形码"
                ></el-input>
              </el-form-item>
            </el-col>
            <!-- <el-col :span="5">

              <el-form-item label="启用状态" prop="status">
                <el-switch
                  v-model="queryModel.status"
                  active-color="#13ce66"
                  inactive-color="#dbdfe6"
                  active-value="1"
                  inactive-value="0"
                ></el-switch>
              </el-form-item>
            </el-col> -->
            <el-col :span="4">
              <el-form-item label="启用状态" prop="status">
                <el-select
                  v-model="queryModel.status"
                  value-key="value"
                  filterable
                  clearable
                  placeholder="请选择启用状态"
                >
                  <el-option
                    v-for="item in status"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value"
                  ></el-option>
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="3" style="display:flex;justivy-content:space-around">
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
            <el-col :span="5" style="text-align:right;padding-right:10px">
              <el-button-group>
                <el-dropdown v-show="permission.add">
                  <el-button type="primary">
                     添加 <i class="el-icon-arrow-down el-icon--right"></i>
                  </el-button>
                  <el-dropdown-menu slot="dropdown">
                    <el-dropdown-item>
                      <el-button type="text" @click="onSynchronousDrug()">同步</el-button>
                    </el-dropdown-item>
                    <el-dropdown-item>
                      <el-button type="text" @click="onCreateDrug()">自建</el-button>
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </el-dropdown>
                <el-dropdown v-show="permission.add" style="padding-left:10px">
                    <span class="el-dropdown-link">
                      批量操作<i class="el-icon-arrow-down el-icon--right"></i>
                    </span>
                  <el-dropdown-menu slot="dropdown">
                    <el-dropdown-item>
                      <el-button type="text" @click="indateClick">批量设置有效期预警</el-button>
                    </el-dropdown-item>
                    <el-dropdown-item>
                      <el-button type="text" @click="inventoryClick">批量设置库存预警</el-button>
                    </el-dropdown-item>
                    <el-dropdown-item>
                      <el-button type="text" @click="importStudentExcel">批量导入</el-button>
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </el-dropdown>
              </el-button-group>
            </el-col>
          </el-form>
        </el-row>
        <QueryForm
          v-else
          v-model="moreParm"
          :tableId="tableId"
          :schemeId="schemeId"
          :routerId="$route.meta.routerId"
          @search="onSearch()"
          @moreCodition="onMoreCodition()"
        ></QueryForm>
      </div>
      <!--  搜索栏  结束 -->

      <!-- 工具栏 开始 -->

      <!-- 工具栏 结束 -->

      <!-- 表格栏  开始 -->
      <el-row>
        <el-col :span="24">
          <div @mouseleave="moveTableOutside">
            <el-table
              class="drag_table"
              :data="drugList"
              border
              height="calc(100vh - 254px)"
              @sort-change="onSortChange"
              @header-dragend="onChangeWidth"
              :cell-class-name="cellClassName"
              :header-cell-class-name="headerCellClassName"
              highlight-current-row
              ref="mutipleTable"
            >
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
                sortable="custom"
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
                label="可用库存"
                sortable="custom"
                width="80px"
              >
                <template slot-scope="scope">
                  <span v-if="scope.row.stock.surplusStock != null">
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
                label="规格"
                sortable="custom"
                width="80px"
              >
                <template slot-scope="scope">
                  <span>{{
                      `${scope.row.dosis}${scope.row.dosisUnit.name}*${scope.row.preparation}${scope.row.preparationUnit.name}/${scope.row.pack.name}`
                    }}</span>
                </template>
              </el-table-column>
              <!-- <el-table-column
                prop="inventory"
                label="库存"
                sortable="custom"
                width="80px"
              >
              <template slot-scope="scope">
                <span>
                  {{
                    Math.floor(scope.row.inventory / scope.row.preparation) >= 0 ?
                           Math.floor(scope.row.inventory / scope.row.preparation) + scope.row.pack.name + ((scope.row.inventory % scope.row.preparation > 0) ? (scope.row.inventory % scope.row.preparation) +  scope.row.preparationUnit.name: "") : scope.row.inventory +  scope.row.preparationUnit.name
                  }}
                </span>
              </template>
              </el-table-column> -->
              <!-- <el-table-column
                prop="stockUnit"
                label="单位"
                sortable="custom"
                width="80px"
              >
              </el-table-column> -->

              <!--表行级操作按钮-->
              <el-table-column
                label="操作"
                header-align="center"
                width="120px"
              >
                <template slot="header" slot-scope="scope">
                  <span>操作</span>
                  <view-columns-select
                    v-model="columnViews"
                    v-on:save-column-view="saveColumn"
                    v-on:show-all-column="showAllColumn"
                    v-on:show-default-column="showDefaultColumn"
                  ></view-columns-select>
                  <export-excel-button
                    v-show="permission.export"
                    :data="drugList"
                    :tHeader="getHeads()"
                    :filterVal="getFilterVal()"
                    :plain="true"
                  ></export-excel-button>
                </template>
                <template slot-scope="scope">
                  <OperationIcon
                    v-show="permission.view"
                    type="info"
                    content="查看"
                    placement="top-start"
                    icon-name="el-icon-view"
                    @click="onViewDrug(scope.$index, scope.row)"
                  ></OperationIcon>
                  <OperationIcon
                    v-show="permission.edit"
                    type="primary"
                    content="编辑"
                    placement="top-start"
                    icon-name="el-icon-edit"
                    @click="onEditDrug(scope.$index, scope.row)"
                  ></OperationIcon>
                  <OperationIcon
                    v-show="permission.add"
                    type="primary"
                    content="复制"
                    placement="top-start"
                    icon-name="el-icon-document"
                    @click="onCopyDrug(scope.$index, scope.row)"
                  ></OperationIcon>
                  <!-- <OperationIcon
                    v-show="permission.remove"
                    type="danger"
                    content="删除"
                    placement="top-start"
                    icon-name="el-icon-delete"
                    @click="onDeleteDrug(scope.$index, scope.row)"
                  ></OperationIcon> -->
                  <OperationIcon
                    v-show="permission.view"
                    type="info"
                    content="历史记录"
                    placement="top-start"
                    icon-name="el-icon-info"
                    @click="onShowHistory(scope.$index, scope.row)"
                  ></OperationIcon>
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
          <el-pagination
            background
            @size-change="onSizeChange"
            @current-change="onCurrentChange"
            :current-page.sync="currentPage"
            :page-sizes="[20, 50, 100, drugTotal]"
            :page-size="20"
            layout="total, sizes, prev, pager, next, jumper"
            :total="drugTotal"
          >
          </el-pagination>
        </el-col>
      </el-row>
      <!-- 分页栏     结束 -->
    </el-card>

    <!-- 批量设置有效期预警 -->
    <div class="indate">
      <el-dialog
        title="批量设置有效期"
        :visible.sync="indateDialogVisible"
        width="30%"
        :before-close="handleClose">
        <!-- <el-divider style="margin-top:-30px;"></el-divider> -->
        <div style="padding-bottom:10px;margin-top:-20px">
          <el-alert
            title="批量设置后将修改所有药品的效期预警时间"
            :closable="false"
            type="warning">
          </el-alert>
        </div>
        <span>药品有效期的预警时间为</span>
        <el-input v-model="indate" oninput="value=value.replace(/[^\d.]/g,'')">
          <template slot="append">
            天
          </template>
        </el-input>
        <span slot="footer" class="dialog-footer">
        <el-button @click="indateSave(0)">取 消</el-button>
        <el-button type="primary" @click="indateSave(1)">确 定</el-button>
      </span>
      </el-dialog>

      <!-- 库存预警 -->
      <el-dialog
        title="批量设置库存预警"
        :visible.sync="inventoryDialogVisible"
        width="30%"
        :before-close="handleCloseInventory">
        <!-- <el-divider style="margin-top:-30px;"></el-divider> -->
        <div style="padding-bottom:10px;margin-top:-20px">
          <el-alert
            title="批量设置后将修改所有药品的库存预警数量（按包装代为预警）"
            :closable="false"
            type="warning">
          </el-alert>
        </div>
        <span>药品的库存预警数量为</span>
        <el-input v-model="inventoryFloor" oninput="value=value.replace(/[^\d.]/g,'')">
        </el-input>
        <span slot="footer" class="dialog-footer">
        <el-button @click="inventorySave(0)">取 消</el-button>
        <el-button type="primary" @click="inventorySave(1)">确 定</el-button>
      </span>
      </el-dialog>

      <!-- 点击导入后弹出框 -->
      <el-dialog title="导入药品" :visible.sync="importDialogVisible" width="45%" :before-close="cancellation">
        <input ref="file" type="file" accept=".xlsx,.xls" style="display: none;" @change="uploadFile">
        <el-button type="primary" icon="el-icon-upload" @click="clickFile">选择文件</el-button>
        <el-button type="primary" icon="el-icon-download" @click="downloadExcel">模板下载</el-button>
        <el-button @click="cancellation()">取 消</el-button>
      </el-dialog>

      <el-dialog title="药品信息导入错误" :visible.sync="dialogVisible" width="36%" :before-close="mistakeClose">
        <span> 导入成功<span style="white-space:pre;color:#08ff00;font-size: 16px">{{chengGong}}</span>条&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span>
        <span> 导入失败<span style="white-space:pre;color:red;font-size: 16px">{{shiBai}}</span>条 </span><br>
        <span style="white-space:pre;color:red;font-size: 15px">{{mistake}}</span>
        <span slot="footer" class="dialog-footer">
          <el-button @click="dialogVisible = false">取 消</el-button>
          <el-button type="primary" @click="dialogVisible = false">确 定</el-button>
        </span>
      </el-dialog>
    </div>
  </el-row>
</template>

<script>
  import {validatenull} from "@/utils/validate";
  import {
    listByCompanyDrugPage,
    getDrugById,
    deleteDrug,
    updateAllIndate,
    updateAllInventory,
    inventory,
    uploadExcel
  } from "@/api/stock/drug";
  import {listResourcePermission} from "@/api/admin/common/permission";
  import DrugForm from "./drugForm";
  import {listDictItemAll} from "@/api/sys/dictItem";
  import ExportExcelButton from "@/components/ExportExcelButton";
  import ViewColumnsSelect from "@/views/components/ViewColumnsSelect";
  import QueryForm from "@/views/components/queryForm";
  import MainUI from "@/views/components/mainUI";
  import OperationIcon from "@/components/OperationIcon";
  import History from "@/views/components/history";
  import {getLocalCurrentCompany} from "@/utils/auth";
  import fileurl from "@/assets/file/drug.xlsx";
  import DrugListSynchronization from './drugListSynchronization'


  export default {
    extends: MainUI,
    components: {
      DrugForm,
      ExportExcelButton,
      ViewColumnsSelect,
      QueryForm,
      OperationIcon,
      History,
      DrugListSynchronization
    },
    data(row) {
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
          export: false,
        },
        queryTypes: {
          goods_name: "like",
          type: "=",
          bar_code: "like",
        },
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
        status: [
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
              value: currentUser.company.id,
            },
          ],
          offset: 0,
          limit: 20,
          columnName: "", // 排序字段名
          order: "", // 排序
        },
        currentPage: 1,
        drugTotal: 0,
        drugList: [],

        type_List: [], // 药品类型

        oprColumnWidth: 140, // 操作列宽
        tableId: "1004078055755374623",
        schemeId: "1004078055755374645",
        indateDialogVisible: false,  //批量设置有效期弹出框
        inventoryDialogVisible: false,   //批量设置库存预警弹出框
        importDialogVisible: false, // 批量导入弹框变量
        indate: "",   //有效期
        inventoryFloor: ""   //库存下限
      };
    },
  methods: {
      // 同步药品信息
      handleSync() {
        this.getDrugList();
        this.$message.success("同步成功")
      },
      handleClose() {
        this.indateSave(0)
      },
      handleCloseInventory() {
        this.inventorySave(0)
      },
      //批量设置有效期保存
      indateSave(index) {
        if (index == 0) {
          this.indateDialogVisible = false
          this.indate = ""
        } else {
          console.log(this.indate);
          let drug = {
            indate: this.indate,
            company: currentUser.company
          }
          updateAllIndate(drug).then((res) => {
            if (res.code == 100) {
              this.indateDialogVisible = false
              this.indate = ""
              this.getDrugList();
              this.$message.success("修改成功")
            }
          }).catch((error) => {
            this.$message.error(error)
          })
        }
      },

      //批量设置库存预警保存
      inventorySave(index) {
        if (index == 0) {
          this.inventoryDialogVisible = false
          this.inventoryFloor = ""
        } else {
          let drug = {
            inventoryFloor: this.inventoryFloor,
            company: currentUser.company
          }
          updateAllInventory(drug).then((res) => {
            if (res.code == 100) {
              this.inventoryDialogVisible = false
              this.inventoryFloor = ""
              this.getDrugList();
              this.$message.success("修改成功")
            }
          }).catch((error) => {
            this.$message.error(error)
          })
        }
      },

      // 下载模板
      downloadExcel() {
        let a = document.createElement('a');
        let evt = document.createEvent('MouseEvents');
        a.download = '药品信息';
        a.href = fileurl;
        evt.initEvent('click', true, true);
        a.dispatchEvent(evt);
        window.URL.revokeObjectURL(a.href);
      },

      // 关闭导入报错
      mistakeClose(done) {
        this.$confirm('确认关闭？')
          .then(_ => {
            done();
          })
          .catch(_ => {
          });
      },

      // 批量导入按钮点击事件
      importStudentExcel() {
        this.importDialogVisible = true;
        console.log(this.importDialogVisible)
      },

      // 选择文件事件
      clickFile() {
        this.$refs.file.dispatchEvent(new MouseEvent('click'))
      },

      // 上传文件
      async uploadFile() {
        const file = this.$refs.file.files
        var extName = file[0].name.substring(file[0].name.lastIndexOf('.')).toLowerCase()
        if (extName === '.xlsx' || extName === '.xls') {
          var formData = new FormData()
          let id = currentUser.company.id;
          console.log("看看这里呀呀呀" + id)
          formData.append('file', file[0])
          this.$message.success('正在导入中，请耐心等待')
          uploadExcel(formData).then((res) => {
            console.log("到这里了吗extName === " + formData)
            console.log("看看报错什么", res.data[0])
            if (res.code === '100') {
              if (res.data[2] === ""){
                this.$message({
                  type: 'success',
                  message: '数据导入成功,共导入'+ res.data[0] +'条信息!'
                })
                this.cancellation();
              }else {
                this.chengGong = res.data[0]
                this.shiBai = res.data[1]
                this.mistake = res.data[2]
                this.dialogVisible = true
                console.log("导入报错"+this.mistake)
                this.cancellation();
              }

            } else {
              this.$message({
                type: "error",
                message: res.resultJson.msg
              })
              this.cancellation();
            }
          })
        } else {
          this.$message.error('数据导入失败，请选择正确的xlsx模板文件')
          this.cancellation();
        }
        this.load();
      },

      // 关闭导入窗口
      cancellation() {
        this.importDialogVisible = false;
        this.reset();
      },

      //批量设置库存弹出框
      inventoryClick() {
        this.inventoryDialogVisible = true
      },

      //批量设置有效期弹出框
      indateClick() {
        this.indateDialogVisible = true
      },

      indexMethod(index) {
        return (this.currentPage - 1) * this.search.limit + index + 1;
      },
      reset() {
        this.$refs.queryForm.resetFields()
        this.onSearch()
      },
      getDrugList(val) {
        this.setLoad();
        /* 查询参数 和数据权限 */
        this.search.params = [
          {
            columnName: "company_id",
            queryType: "=",
            value: currentUser.company.id,
          },
        ];

        if (val == "1") {
          this.search.params = this.search.params.concat(
            this.compositeCondition()
          );
        } else {
          // 查询参数: 药品名称
          this.search.params.push({
            columnName: "goods_name",
            queryType: "like",
            value: this.queryModel.goodsName,
          });
          // 查询参数: 药品类型
          this.search.params.push({
            columnName: "type",
            queryType: "=",
            value: validatenull(this.queryModel.type.value)
              ? ""
              : this.queryModel.type.value,
          });
          // 查询参数: 条形码
          this.search.params.push({
            columnName: "bar_code",
            queryType: "like",
            value: this.queryModel.barCode,
          });
          // 查询参数: 状态
          this.search.params.push({
            columnName: "status",
            queryType: "=",
            value: this.queryModel.status,
          });
        }
        // 数据权限: 药品信息drug
        this.pushDataPermissions(
          this.search.params,
          this.$route.meta.routerId,
          this.tableId
        );
        listByCompanyDrugPage(this.search)
          .then((responseData) => {
            if (responseData.code == 100) {
              this.drugTotal = responseData.data.total;
              this.drugList = responseData.data.rows;
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
          this.getDrugList();
        } else {
          this.$refs["queryForm"].validate((valid) => {
            if (valid) {
              this.search.offset = 0;
              this.currentPage = 1;
              this.getDrugList();
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
        this.getDrugList();
      },
      onCurrentChange(val) {

        this.search.offset = (val - 1) * this.search.limit;
        this.currentPage = val;
        this.getDrugList();
      },
      async pageInit() {
        this.setLoad();
        console.log(currentUser, "///");
        try {
          this.initOptions(this.queryModel);
          this.search.params = [
            {
              columnName: "company_id",
              queryType: "=",
              value: currentUser.company.id,
            },
          ];
          // 数据权限: 药品信息drug
          this.pushDataPermissions(
            this.search.params,
            this.$route.meta.routerId,
            this.tableId
          );
          let [listDrugRespData, listPermissionRespData] = await Promise.all([
            listByCompanyDrugPage(this.search),
            listResourcePermission(this.$route.meta.routerId),
          ]);
          if (
            listDrugRespData.code == 100 &&
            listPermissionRespData.code == 100
          ) {
            this.drugTotal = listDrugRespData.data.total;
            this.drugList = listDrugRespData.data.rows;
            console.log(this.drugList, 'drugNew');
            this.permission.view = listPermissionRespData.data.find((item) => {
              return item.permission === "drugNew:read";
            });
            this.permission.export = listPermissionRespData.data.find((item) => {
              return item.permission === "drugNew:export";
            });
            this.permission.add = listPermissionRespData.data.find((item) => {
              return item.permission === "drugNew:create";
            });
            this.permission.edit = listPermissionRespData.data.find((item) => {
              return item.permission === "drugNew:update";
            });
            this.permission.remove = listPermissionRespData.data.find((item) => {
              return item.permission === "drugNew:delete";
            });
          } else {
            this.showMessage(
              listPermissionRespData.code != 100
                ? listPermissionRespData
                : listDrugRespData
            );
          }
          this.resetLoad();
        } catch (error) {
          this.outputError(error);
        }
      },
      onViewDrug(index, row) {
        this.setLoad();
        getDrugById(row.id)
          .then((responseData) => {
            if (responseData.code == 100) {
              console.log(responseData.data, 'meiyou');
              this.$refs.drugForm.$emit("openViewDrugDialog", responseData.data);
            } else {
              this.showMessage(responseData);
            }
            this.resetLoad();
          })
          .catch((error) => {
            this.outputError(error);
          });
      },
      onCreateDrug() {
        this.$refs.drugForm.$emit("openAddDrugDialog");
      },
      onSynchronousDrug() {
        this.$refs.drugListSynchronization.$emit("openMigratingDrugDialog");
      },
      SurplusStock(row) {
        this.systemParamConfigSearch.params = [
          {
            columnName: "company_id",
            queryType: "=",
            value: getLocalCurrentCompany().id
          },
          {
            columnName: "id",
            queryType: "=",
            value: row.id
          }
        ]
        console.log(row.id)
        inventory(this.systemParamConfigSearch).then(responseData => {
          if (responseData.code == 100) {
            if (responseData.data.length >= 1) {
              responseData.data.forEach(data => {
                this.inventory = data.stock.surplusStock
              })

            }
          } else {
            this.showMessage(responseData)
          }
        }).catch(error => {
          this.outputError(error)
        })
      },
      OpenEdit(index, row) {
        if (this.inventory !== 0) {
          console.log(this.inventory, "有库存")
          this.$message({
            message: "物品存在库存，不能修改",
            type: 'warning'
          })
        } else if (this.inventory === 0) {
          this.setLoad();
          getDrugById(row.id)
            .then((responseData) => {
              if (responseData.code == 100) {
                this.$refs.drugForm.$emit("openEditDrugDialog", responseData.data);
              } else {
                this.showMessage(responseData);
              }
              this.resetLoad();
            })
            .catch((error) => {
              this.outputError(error);
            });
        }
        this.inventory = null
      },
      async onEditDrug(index, row) {
        await this.SurplusStock(row)
        setTimeout(() => {
          this.OpenEdit(index, row)
        }, 1000);
      },
      onCopyDrug(index, row) {
        this.setLoad();
        getDrugById(row.id)
          .then((responseData) => {
            if (responseData.code == 100) {
              this.$refs.drugForm.$emit("openCopyDrugDialog", responseData.data);
            } else {
              this.showMessage(responseData);
            }
            this.resetLoad();
          })
          .catch((error) => {
            this.outputError(error);
          });
      },
      onDeleteDrug(index, row) {
        this.$confirm("确定删除吗？", "确认", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning",
        })
          .then(() => {
            this.setLoad();
            deleteDrug(row)
              .then((responseData) => {
                if (responseData.code == 100) {
                  this.getDrugList();
                  this.showMessage({type: "success", msg: "删除成功"});
                } else {
                  this.showMessage(responseData);
                }
                this.resetLoad();
              })
              .catch((error) => {
                this.outputError(error);
              });
          })
          .catch(() => {
          });
      },
      onSortChange(orderby) {
        if (validatenull(orderby.prop)) {
          this.search.columnName = "";
          this.search.order = "";
        } else {
          this.search.columnName = orderby.prop;
          this.search.order = orderby.order === "descending" ? "desc" : "asc";
        }

        this.getDrugList();
      },
      initOptions(This) {
        let type_search = {
          params: [
            {
              columnName: "dict_type_id",
              queryType: "=",
              value: "1004078055755374603",
            },
          ],
        };
        // 响应字段的条件操作符，替换成触发字段的操作符
        type_search.params.forEach((item) => {
          if (this.queryTypes[item.columnName]) {
            item.queryType = this.queryTypes[item.columnName];
          }
        });
        // 字段对应表上filter条件
        type_search.params.push.apply(type_search.params, []);
        // 数据权限: 字典项sys_dict_item
        this.pushDataPermissions(
          type_search.params,
          this.$route.meta.routerId,
          "4005"
        );
        this.type_List.splice(0, this.type_List.length);
        listDictItemAll(type_search).then((responseData) => {
          this.type_List = responseData.data;
        });
      },
    },
    watch: {
      drugList(val) {
        if (val) {
          this.$nextTick(() => {
            this.$refs.mutipleTable.doLayout();
          });
        }
      },
      // tableData是el-table绑定的数据
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
  mounted() {
      this.pageInit();
      this.onSearch();
    },
  };
</script>
<style lang="scss" scoped>
  .page-container {
    padding: 0;
  }
  .crimson-text {
      color: crimson;
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

  .indate /deep/ .el-dialog__header {
    border-bottom: 1px solid rgb(214, 214, 214) !important;
  }
</style>
