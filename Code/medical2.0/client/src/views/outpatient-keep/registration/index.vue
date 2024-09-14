<template>
  <el-row v-loading="loading">
    <!-- 历史记录  -->
    <History :bussObject="curentRow"></History>
    <!-- 编辑窗口  -->
    <registration-form
      ref="registrationForm"
      :permission="permission"
      v-on:save-finished="getRegistrationList()"
    ></registration-form>
    <div class="page-container">
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
            <el-col :span="6">
              <el-form-item label="科室" prop="department.id">
                <el-cascader
                  v-model="queryModel.department.id"
                  :options="department_List"
                  :key="key_department"
                  :props="{
                    value: 'id',
                    label: 'name',
                    checkStrictly: true,
                    emitPath: false,
                  }"
                  filterable
                  clearable
                  placeholder="请选择科室"
                />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item label="患者" prop="patientId">
                <el-select
                  v-model="queryModel.patientId"
                  value-key="id"
                  filterable
                  clearable
                  placeholder="请选择患者"
                >
                  <el-option
                    v-for="patientId in patientId_List"
                    :key="patientId.id"
                    :label="patientId.name"
                    :value="patientId"
                  ></el-option>
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item label="医生" prop="doctor">
                <el-select
                  v-model="queryModel.doctor"
                  value-key="id"
                  filterable
                  clearable
                  placeholder="请选择医生"
                >
                  <el-option
                    v-for="doctor in doctor_List"
                    :key="doctor.id"
                    :label="doctor.name"
                    :value="doctor"
                  ></el-option>
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-button-group>
                <el-tooltip effect="light" content="搜索" placement="top-start">
                  <el-button
                    type="primary"
                    icon="el-icon-search"
                    @click="onSearch()"
                    :plain="true"
                  ></el-button>
                </el-tooltip>
                <el-tooltip effect="light" content="重置" placement="top-start">
                  <el-button
                    type="primary"
                    icon="el-icon-refresh-left"
                    @click="$refs.queryForm.resetFields()"
                    :plain="true"
                  ></el-button>
                </el-tooltip>
                <el-tooltip effect="light" content="更多" placement="top-start">
                  <el-button
                    type="primary"
                    icon="el-icon-d-arrow-right"
                    @click="onMoreCodition()"
                    :plain="true"
                  ></el-button>
                </el-tooltip>
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
      <div class="page-container-header-end">
        <div>
          <el-button
            v-show="permission.add"
            type="primary"
            icon="el-icon-plus"
            :plain="true"
            @click="onCreateRegistration()"
            >添加</el-button
          >
        </div>
      </div>
      <!-- 工具栏 结束 -->

      <!-- 表格栏  开始 -->
      <el-row>
        <el-col :span="24">
          <div @mouseleave="moveTableOutside">
            <el-table
              class="drag_table"
              :data="registrationList"
              border
              @sort-change="onSortChange"
              @header-dragend="onChangeWidth"
              :cell-class-name="cellClassName"
              :header-cell-class-name="headerCellClassName"
              highlight-current-row
            >
              <el-table-column
                v-for="(cv, index) in columnViews"
                v-if="cv.display"
                :prop="cv.prop"
                :key="`columnViews_${index}`"
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
              <!--表行级操作按钮-->
              <el-table-column
                label="操作"
                header-align="center"
                :width="oprColumnWidth + 'px'"
                fixed="right"
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
                    :data="registrationList"
                    :tHeader="getHeads()"
                    :filterVal="getFilterVal()"
                    :plain="true"
                  ></export-excel-button>
                </template>
                <template slot-scope="scope">
                  <!-- 依旧枕 -->
                  <p
                    class="btn"
                    v-if="scope.row.status.value === 'registrationStatus_3'"
                  >
                    <span @click="onEditRegistration(scope.$index, scope.row)"
                      >修改</span
                    ><span
                      @click="
                        updateStatusId(scope.row.id, scope.row.status.value)
                      "
                      >签到</span
                    >
                  </p>
                  <p
                    class="btn1"
                    v-if="scope.row.status.value === 'registrationStatus_0'"
                  >
                    <span @click="onEditRegistration(scope.$index, scope.row)"
                      >修改</span
                    ><span @click="showRefundRegistrationDialog(scope.row)"
                      >退号</span
                    >
                  </p>
                  <p
                    class="btn2"
                    v-if="
                      scope.row.status.value === 'registrationStatus_1' ||
                      scope.row.status.value === 'registrationStatus_2'
                    "
                    @click="onViewRegistration(scope.$index, scope.row)"
                  >
                    查看
                  </p>
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
            :page-sizes="[10, 20, 50, 100, registrationTotal]"
            :page-size="10"
            layout="total, sizes, prev, pager, next, jumper"
            :total="registrationTotal"
          >
          </el-pagination>
        </el-col>
      </el-row>
      <!-- 分页栏     结束 -->
      <!--退费弹框 开始-->
      <el-dialog
        title="退费信息"
        :visible.sync="refundRegistraionVisible"
        width="50%"
        :close-on-click-modal="false"
        :before-close="dialogBeforeClose"
      >
        <div>
          <el-divider></el-divider>
          <div id="refundTotalFee_Container">
            <div id="refundTotalFee_Title">应退金额（元）：</div>
            <div id="refundTotalFee_Fee">{{ refundTotalFee }}</div>
          </div>
          <div class="refund-registration-flex-start">
            <p class="refund-registration-p">支付方式</p>
            <el-radio-group v-model="refundReistrationPayType" class="refund-registration-content">
              <el-radio
                v-for="payType in payType_List"
                :key="payType.value"
                :label="payType.value"
                >{{ payType.name }}</el-radio
              >
            </el-radio-group>
          </div>
          <div class="refund-registration-flex-start">
            <p class="refund-registration-p">备注</p>
            <el-input v-model="refundRegistrationRemarks"  class="refund-registration-content"></el-input>
          </div>
          <el-divider></el-divider>
        </div>
        <div slot="footer">
          <el-button @click="refundRegistraionVisible = false">取 消</el-button>
          <el-button type="primary" @click="refundRegistrationPayClick"
            >确认退费</el-button
          >
        </div>
      </el-dialog>
      <!--退费弹框 结束-->
    </div>
  </el-row>
</template>

<script>
import { validatenull } from "@/utils/validate";
import {
  listRegistrationPage,
  getRegistrationById,
  deleteRegistration,
  updateStatus,
  refundRegistrationPay,
} from "@/api/outpatient/registration";
import { listResourcePermission } from "@/api/admin/common/permission";
import RegistrationForm from "./registrationForm";
import { treeDepartment } from "@/api/org/department";
import { listPatientAll } from "@/api/outpatient/patient";
import { listUserAll } from "@/api/admin/user";
import ExportExcelButton from "@/components/ExportExcelButton";
import ViewColumnsSelect from "@/views/components/ViewColumnsSelect";
import QueryForm from "@/views/components/queryForm";
import MainUI from "@/views/components/mainUI";
import OperationIcon from "@/components/OperationIcon";
import History from "@/views/components/history";
import { listDictItemAll } from "@/api/sys/dictItem";

export default {
  extends: MainUI,
  components: {
    RegistrationForm,
    ExportExcelButton,
    ViewColumnsSelect,
    QueryForm,
    OperationIcon,
    History,
  },
  data() {
    return {
      permission: {
        view: false,
        add: false,
        edit: false,
        remove: false,
        export: false,
      },
      queryTypes: {
        department_id: "=",
        patient_id: "=",
        doctor_id: "=",
      },
      queryModel: {
        department: {
          // 科室
          id: null,
          name: "",
        },
        patientId: {
          // 父表 患者
          id: null,
          name: "",
        },
        doctor: {
          // 医生
          id: null,
          name: "",
        },
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
        limit: 10,
        columnName: "", // 排序字段名
        order: "", // 排序
      },
      currentPage: 1,
      registrationTotal: 0,
      registrationList: [],

      key_department: 1, // el-cascader key
      department_List: [], // 科室
      patientId_List: [], // 患者
      doctor_List: [], // 医生

      oprColumnWidth: 140, // 操作列宽
      tableId: "1008534118685450402",
      schemeId: "1008534118685450425",

      payType_List: [], //支付类型集合
      refundRegistrationId:0,
      refundRegistraionVisible: false, //是否显示退挂号费弹框
      refundTotalFee: 0, //退费费用金额
      refundReistrationPayType: "", //退费支付方式
      refundRegistrationRemarks: "", //退费备注信息
      refundStatus: "registrationStatus_2", //退费状态默认值
    };
  },
  methods: {
    async refundRegistrationPayClick() {
      const res = await refundRegistrationPay(
        this.refundRegistrationId,
        this.refundStatus,
        this.refundReistrationPayType,
        this.refundRegistrationRemarks
      );
      if (res.code == "100") {
        this.getRegistrationList();
        this.refundRegistraionVisible = false;
      }
    },
    //显示退费弹框
    showRefundRegistrationDialog(refundData) {
      this.refundRegistrationId = refundData.id;
      this.refundTotalFee = refundData.registrationFee;
      this.refundReistrationPayType = refundData.payType.value;
      this.refundRegistrationRemarks = "";
      this.refundRegistraionVisible = true;
    },
    async updateStatusId(id, status) {
      let status1;
      if (status === "registrationStatus_0") status1 = "registrationStatus_2";
      if (status === "registrationStatus_3") status1 = "registrationStatus_0";
      const res = await updateStatus(id, status1);
      if (res.code === "100") {
        this.getRegistrationList();
      }
    },
    getRegistrationList() {
      this.setLoad();
      /* 查询参数 和数据权限 */
      this.search.params = [
        {
          columnName: "company_id",
          queryType: "=",
          value: currentUser.company.id,
        },
      ];
      if (this.moreCodition) {
        this.search.params = this.search.params.concat(
          this.compositeCondition()
        );
      } else {
        // 查询参数: 科室
        this.search.params.push({
          columnName: "department_id",
          queryType: "=",
          value: validatenull(this.queryModel.department.id)
            ? ""
            : this.queryModel.department.id,
        });
        // 查询参数: 患者
        this.search.params.push({
          columnName: "patient_id",
          queryType: "=",
          value: validatenull(this.queryModel.patientId.id)
            ? ""
            : this.queryModel.patientId.id,
        });
        // 查询参数: 医生
        this.search.params.push({
          columnName: "doctor_id",
          queryType: "=",
          value: validatenull(this.queryModel.doctor.id)
            ? ""
            : this.queryModel.doctor.id,
        });
      }
      // 数据权限: 挂号表registration
      this.pushDataPermissions(
        this.search.params,
        this.$route.meta.routerId,
        this.tableId
      );

      console.log(this.search);
      listRegistrationPage(this.search)
        .then((responseData) => {
          if (responseData.code == 100) {
            this.registrationTotal = responseData.data.total;
            this.registrationList = responseData.data.rows;
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
        this.getRegistrationList();
      } else {
        this.$refs["queryForm"].validate((valid) => {
          if (valid) {
            this.search.offset = 0;
            this.currentPage = 1;
            this.getRegistrationList();
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
      this.getRegistrationList();
    },
    onCurrentChange(val) {
      this.search.offset = (val - 1) * this.search.limit;
      this.currentPage = val;
      this.getRegistrationList();
    },
    async pageInit() {
      this.setLoad();
      try {
        this.initOptions(this.queryModel);
        this.search.params = [
          {
            columnName: "company_id",
            queryType: "=",
            value: currentUser.company.id,
          },
        ];
        // 数据权限: 挂号表registration
        this.pushDataPermissions(
          this.search.params,
          this.$route.meta.routerId,
          this.tableId
        );
        let [listRegistrationRespData, listPermissionRespData] =
          await Promise.all([
            listRegistrationPage(this.search),
            listResourcePermission(this.$route.meta.routerId),
          ]);
        if (
          listRegistrationRespData.code == 100 &&
          listPermissionRespData.code == 100
        ) {
          this.registrationTotal = listRegistrationRespData.data.total;
          this.registrationList = listRegistrationRespData.data.rows;
          this.permission.view = listPermissionRespData.data.find((item) => {
            return item.permission === "registration:read";
          });
          this.permission.export = listPermissionRespData.data.find((item) => {
            return item.permission === "registration:export";
          });
          this.permission.add = listPermissionRespData.data.find((item) => {
            return item.permission === "registration:create";
          });
          this.permission.edit = listPermissionRespData.data.find((item) => {
            return item.permission === "registration:update";
          });
          this.permission.remove = listPermissionRespData.data.find((item) => {
            return item.permission === "registration:delete";
          });
        } else {
          this.showMessage(
            listPermissionRespData.code != 100
              ? listPermissionRespData
              : listRegistrationRespData
          );
        }
        this.resetLoad();
      } catch (error) {
        this.outputError(error);
      }
    },
    onViewRegistration(index, row) {
      this.setLoad();
      getRegistrationById(row.id)
        .then((responseData) => {
          if (responseData.code == 100) {
            this.$refs.registrationForm.$emit(
              "openViewRegistrationDialog",
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
    onCreateRegistration() {
      this.$refs.registrationForm.$emit("openAddRegistrationDialog");
    },
    onEditRegistration(index, row) {
      this.setLoad();
      getRegistrationById(row.id)
        .then((responseData) => {
          if (responseData.code == 100) {
            this.$refs.registrationForm.$emit(
              "openEditRegistrationDialog",
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
    onCopyRegistration(index, row) {
      this.setLoad();
      getRegistrationById(row.id)
        .then((responseData) => {
          if (responseData.code == 100) {
            this.$refs.registrationForm.$emit(
              "openCopyRegistrationDialog",
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
    onDeleteRegistration(index, row) {
      this.$confirm("确定删除吗？", "确认", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      })
        .then(() => {
          this.setLoad();
          deleteRegistration(row)
            .then((responseData) => {
              if (responseData.code == 100) {
                this.getRegistrationList();
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
        .catch(() => {});
    },
    onSortChange(orderby) {
      if (validatenull(orderby.prop)) {
        this.search.columnName = "";
        this.search.order = "";
      } else {
        this.search.columnName = orderby.prop;
        this.search.order = orderby.order === "descending" ? "desc" : "asc";
      }

      this.getRegistrationList();
    },
    initOptions(This) {
      this.key_department++;
      let department_search = {
        params: [
          {
            columnName: "company_id",
            queryType: "=",
            value: currentUser.company.id,
          },
        ],
      };
      // 响应字段的条件操作符，替换成触发字段的操作符
      department_search.params.forEach((item) => {
        if (this.queryTypes[item.columnName]) {
          item.queryType = this.queryTypes[item.columnName];
        }
      });
      // 字段对应表上filter条件
      department_search.params.push.apply(department_search.params, []);
      // 数据权限: 部门org_department
      this.pushDataPermissions(
        department_search.params,
        this.$route.meta.routerId,
        "52676365136650250"
      );
      this.department_List.splice(0, this.department_List.length);
      treeDepartment(department_search).then((responseData) => {
        this.department_List = responseData.data;
      });
      let patientId_search = {
        params: [],
      };
      // 响应字段的条件操作符，替换成触发字段的操作符
      patientId_search.params.forEach((item) => {
        if (this.queryTypes[item.columnName]) {
          item.queryType = this.queryTypes[item.columnName];
        }
      });
      // 字段对应表上filter条件
      patientId_search.params.push.apply(patientId_search.params, [
        {
          columnName: "company_id",
          queryType: "=",
          value: currentUser.company.id,
        },
      ]);
      // 数据权限: 患者表patient
      this.pushDataPermissions(
        patientId_search.params,
        this.$route.meta.routerId,
        "1008489176147648530"
      );
      this.patientId_List.splice(0, this.patientId_List.length);
      listPatientAll(patientId_search).then((responseData) => {
        this.patientId_List = responseData.data;
      });
      let doctor_search = {
        params: [
          {
            columnName: "department_id",
            queryType: "=",
            value: currentUser.department.id,
          },
        ],
      };
      // 响应字段的条件操作符，替换成触发字段的操作符
      doctor_search.params.forEach((item) => {
        if (this.queryTypes[item.columnName]) {
          item.queryType = this.queryTypes[item.columnName];
        }
      });
      // 字段对应表上filter条件
      doctor_search.params.push.apply(doctor_search.params, []);
      // 数据权限:  用户sys_user
      this.pushDataPermissions(
        doctor_search.params,
        this.$route.meta.routerId,
        "4004"
      );
      this.doctor_List.splice(0, this.doctor_List.length);
      listUserAll(doctor_search).then((responseData) => {
        this.doctor_List = responseData.data;
      });

      //支付类型查询
      let payType_search = {
        params: [
          {
            columnName: "dict_type_id",
            queryType: "=",
            value: "1008793465990693120",
          },
        ],
      };
      // 字段对应表上filter条件
      payType_search.params.push.apply(payType_search.params, []);
      // 数据权限: 字典项sys_dict_item
      this.pushDataPermissions(
        payType_search.params,
        this.$route.meta.routerId,
        "4005"
      );
      this.payType_List.splice(0, this.payType_List.length);
      listDictItemAll(payType_search).then((responseData) => {
        this.payType_List = responseData.data;
      });
    },
  },
  watch: {
    "queryModel.department.id": function (val, oldVal) {
      if (val != oldVal) {
        let This = this.queryModel;
        this.queryModel.doctor = {
          // 医生
          id: null,
          name: null,
        };
        let doctor_search = {
          params: [
            {
              columnName: "department_id",
              queryType: "=",
              value: currentUser.department.id,
            },
          ],
        };
        // 响应字段的条件操作符，替换成触发字段的操作符
        doctor_search.params.forEach((item) => {
          if (this.queryTypes[item.columnName]) {
            item.queryType = this.queryTypes[item.columnName];
          }
        });
        // 字段对应表上filter条件
        doctor_search.params.push.apply(doctor_search.params, []);
        // 数据权限:  用户sys_user
        this.pushDataPermissions(
          doctor_search.params,
          this.$route.meta.routerId,
          "4004"
        );
        this.doctor_List.splice(0, this.doctor_List.length);
        listUserAll(doctor_search).then((responseData) => {
          this.doctor_List = responseData.data;
        });
      }
    },
  },
  mounted() {
    this.pageInit();
  },
};
</script>
<style scoped>
.btn,
.btn1,
.btn2 {
  display: flex;
  justify-content: center;
  align-items: center;
  color: #01a5e4;
}
.btn span:nth-child(1) {
  color: #01a5e4;
  padding: 0px 10px;
}
.btn span:nth-child(2) {
  color: #14ca04;
  padding: 0px 10px;
}

.btn1 span:nth-child(2) {
  color: #e32929;
  padding: 0px 10px;
}
.btn2 {
  color: #01a5e4;
}

#refundTotalFee_Container {
  display: flex;
  justify-content: flex-start;
  padding: 20px;
  margin: 10px;
  background-color: rgba(207, 227, 236, 0.803921568627451);
  border-radius: 7px;
}

#refundTotalFee_Title {
  font-family: "Arial Normal", "Arial", sans-serif;
  font-weight: 400;
  font-style: normal;
  font-size: 21px;
  margin-right: 8px;
}

#refundTotalFee_Fee {
  font-weight: 800;
  font-style: black;
  font-size: 25px;
  margin-top: -4px;
}

.refund-registration-flex-start{
  display:flex;
  justify-content: flex-start;
  padding: 0 20px;
  margin:10px;
}

.refund-registration-p{
  width: 80px;
  text-align: right;
  margin-right: 10px;
}

.refund-registration-content{
  widows: 250px;
  margin-top: 15px;
}
</style>