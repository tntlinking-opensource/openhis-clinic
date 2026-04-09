<template>
  <div>
    <el-row :gutter="10" class="rowcrad">
      <el-col :lg="6" :xs="12" :sm="6" v-if="TobeseenList.length>0">
        <el-card class="box-card" shadow="always">
          <workbench-form ref='workbenchForm' @typeclick="typeclickload"></workbench-form>
          <div class="card-header">
            <span class="cartitle">待就诊</span>
            <div>
              <el-button style="padding: 3px 0" type="text" @click="btnoverlookidlist('djz')">忽略全部</el-button>
              <el-badge :value="Totalsums<10?Totalsums:'10+'" class="badgeitem" v-show="Totalsums>0" @click.native="onCreatePatient('djz')"></el-badge>
            </div>
          </div>
          <div v-for="(item,index) in TobeseenList.slice(0, 3)" :key="index" class="text">
            <a @click="skiptobeseen(item.patientId.id,'djz')" class="cara1">{{ index + 1 }}. {{ item.patientId.name }} / {{ item.patientId.gender.name }} / {{ item.patientId.age}}岁</a>
            <span class="cara2">
              <a @click="skiptobeseen(item.patientId.id,'djz')">就诊</a>
              <a @click="btnoverlook(item.id,'djz')" style="margin-left:5px;">忽略</a>
            </span>
          </div>
        </el-card>
      </el-col>
      <el-col :lg="6" :xs="12" :sm="6" v-if="PatientchargeList.length>0">
        <el-card class="box-card" shadow="always">
          <workbench-form ref='workbenchForm' @typeclick="typeclickload"></workbench-form>
          <div class="card-header">
            <span class="cartitle">待收费</span>
            <div>
              <el-button style="padding: 3px 0" type="text" @click="btnoverlookidlist('dsf')">忽略全部</el-button>
              <el-badge :value="Patientchargesum<10?Patientchargesum:'10+'" class="badgeitem" @click.native="onCreatePatient('dsf')"></el-badge>
            </div>
          </div>
          <div v-for="(item,index) in PatientchargeList.slice(0, 3)" :key="index" class="text">
            <a @click="skiptobeseen(item.patient.id,'dsf')" class="cara1">{{ item.displaycontent }}</a>
            <span class="cara2">
              <a @click="skiptobeseen(item.patient.id,'dsf')">查看</a>
              <a @click="btnoverlook(item.id,'dsf')" style="margin-left:5px;">忽略</a>
            </span>
          </div>
        </el-card>
      </el-col>
      <el-col :lg="6" :xs="12" :sm="6" v-if="listdispensingcounts.length>0">
        <el-card class="box-card" shadow="always">
          <workbench-form ref='workbenchForm' @typeclick="typeclickload"></workbench-form>
          <div class="card-header">
            <span class="cartitle">待发药</span>
            <div>
              <el-button style="padding: 3px 0" type="text" @click="btnoverlookidlist('dfy')">忽略全部</el-button>
              <el-badge :value="dispensingsum<10?dispensingsum:'10+'" class="badgeitem" @click.native="onCreatePatient('dfy')"></el-badge>
            </div>
          </div>
          <div v-for="(item,index) in listdispensingcounts.slice(0, 3)" :key="index" class="text">
            <a @click="skiptobeseen(item.patient.id,'dfy')" class="cara1">{{ item.displaycontent }}</a>
            <span class="cara2">
              <a @click="skiptobeseen(item.patient.id,'dfy')">查看</a>
              <a @click="btnoverlook(item.id,'dfy')" style="margin-left:5px;">忽略</a>
            </span>
          </div>
        </el-card>
      </el-col>
      <el-col :lg="6" :xs="12" :sm="6" v-if="patientcurecounts.length>0">
        <el-card class="box-card" shadow="always">
          <workbench-form ref='workbenchForm' @typeclick="typeclickload"></workbench-form>
          <div class="card-header">
            <span class="cartitle">待治疗</span>
            <div>
              <el-button style="padding: 3px 0" type="text" @click="btnoverlookidlist('dzl')">忽略全部</el-button>
              <el-badge :value="patientcuresum<10?patientcuresum:'10+'" class="badgeitem" @click.native="onCreatePatient('dzl')"></el-badge>
            </div>
          </div>
          <div v-for="(item,index) in patientcurecounts.slice(0, 3)" :key="index" class="text">
            <a @click="skiptobeseen(item.patient.id,'dzl')" class="cara1">{{ item.displaycontent }}</a>
            <span class="cara2">
              <a @click="skiptobeseen(item.patient.id,'dzl')">就诊</a>
              <a @click="btnoverlook(item.id,'dzl')" style="margin-left:5px;">忽略</a>
            </span>
          </div>
        </el-card>
      </el-col>
      <el-col :lg="6" :xs="12" :sm="6" v-if="patientdsylistcounts.length>0">
        <el-card class="box-card" shadow="always">
          <workbench-form ref='workbenchForm' @typeclick="typeclickload"></workbench-form>
          <div class="card-header">
            <span class="cartitle">待输液</span>
            <div>
              <el-button style="padding: 3px 0" type="text" @click="btnoverlookidlist('dsy')">忽略全部</el-button>
              <el-badge :value="patientdsylistsums<10?patientdsylistsums:'10+'" class="badgeitem" @click.native="onCreatePatient('dsy')"></el-badge>
            </div>
          </div>
          <div v-for="(item,index) in patientdsylistcounts.slice(0, 3)" :key="index" class="text">
            <a @click="skiptobeseen(item.patient.id,'dsy')" class="cara1">{{ item.displaycontent }}</a>
            <span class="cara2">
              <a @click="skiptobeseen(item.patient.id,'dsy')">就诊</a>
              <a @click="btnoverlook(item.id,'dsy')" style="margin-left:5px;">忽略</a>
            </span>
          </div>
        </el-card>
      </el-col>
      <el-col :lg="6" :xs="12" :sm="6" v-if="jyjctotal>0">
        <el-card class="box-card" shadow="always">
          <workbench-form ref='workbenchForm' @typeclick="typeclickload"></workbench-form>
          <div class="card-header">
            <span class="cartitle">待检验检查</span>
            <div>
              <el-button style="padding: 3px 0" type="text" @click="btnoverlookidlist('djyjc')">忽略全部</el-button>
              <el-badge :value="jyjctotal<10?jyjctotal:'10+'" class="badgeitem" @click.native="onCreatePatient('djyjc')"></el-badge>
            </div>
          </div>
          <div v-for="(item,index) in jyjclistcounts.slice(0, 3)" :key="index" class="text">
            <a @click="skiptobeseen(item.patient.id,'djyjc')" class="cara1">{{ item.patient.name }} / {{ item.patient.age }} / {{ item.name }}</a>
            <span class="cara2">
              <a @click="skiptobeseen(item.patient.id,'djyjc')">就诊</a>
              <a @click="btnoverlook(item.registration.id,'djyjc')" style="margin-left:5px;">忽略</a>
            </span>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import WorkbenchForm from './workbenchForm'
import MainUI from '@/views/components/mainUI'
import { updateNotShowById } from '@/api/outpatient/recipelInfo'
import History from '@/views/components/history'
import {
  listdispensingPages,
  listSchedulesPage,
  updateoverlockidlist,
  updateoverlockids,
} from "@/api/workbench/Schedules";
import moment from "moment";
import { listInspectionCheckPage } from '@/api/cure/inspectionCheck'
import { listRecipelInfoReviewPage } from '@/api/outpatient/recipelInfoReview'
import { Loading } from 'element-ui'

export default {
  extends: MainUI,
  components: {
    WorkbenchForm: WorkbenchForm,
    History: History
  },

  data: function () {
    return {
      // 待就诊
      TobeseenList: [],
      Totalsums: 0,
      SearchPreModel: {
        columnName: "",
        limit: 10,
        offset: 0,
        order: "",
      },

      // 待发药
      PageRegistration: {
        columnName: "",
        limit: 10,
        offset: 0,
        order: "desc",
        companyId: "",
        recipeStatus: "0",
        updateDate: "2022-06-01 00:00:00",
        status: "registrationStatus_1",
        chargeStatus: 1,
        dispensionStatus: 0,
        recipelType: 'recipelType_3',
        patientName: '',
        patientCode: '',
        overlook: '',
      },
      listdispensingcounts: [],
      dispensingsum: 0,

      // 待收费
      patientQueryCondition: {
        columnName: "",
        limit: 10,
        offset: 0,
        order: "",
        companyId: "",
        recipeStatus: "0",
        updateDate: "2022-06-01 00:00:00",
        status: "registrationStatus_1",
        chargeStatus: "",
        dispensionStatus: 3,
        patientName: null,
        patientCode: null,
        overlook: '',
      },
      PatientchargeList: [],
      Patientchargesum: 0,

      // 待治疗
      patientcure: {
        columnName: "",
        limit: 10,
        offset: 0,
        order: "",
        companyId: "",
        recipeStatus: "0",
        updateDate: "2022-06-01 00:00:00",
        status: "registrationStatus_1",
        chargeStatus: "",
        dispensionStatus: 3,
        patientName: null,
        patientCode: null,
        recipelType: 'recipelType_10',
        cureType: 0,
        overlook: "",
      },
      patientcurecounts: [],
      patientcuresum: 0,

      // 待输液
      patientdsylist: {
        columnName: "",
        limit: 10,
        offset: 0,
        order: "",
        companyId: "",
        recipeStatus: "0",
        updateDate: "2022-06-01 00:00:00",
        status: "registrationStatus_1",
        chargeStatus: 1,
        dispensionStatus: 1,
        patientName: null,
        patientCode: null,
        recipelType: 'recipelType_11',
        infuseType: 0,
        overlook: "",
      },
      patientdsylistcounts: [],
      patientdsylistsums: 0,

      // 待检验检查
      searchjyjc: {
        params: [
          { columnName: 'company_id', queryType: '=', value: currentUser.company.id },
          { columnName: 'status', queryType: '=', value: "0" },
          { columnName: 'registration.overlook', queryType: '=', value: "0" },
        ],
        offset: 0,
        limit: 10,
        columnName: '',
        order: ''
      },
      jyjclistcounts: [],
      jyjctotal: 0,

      // 处方审查
      reviewTotal: 0,
      reviewList: [],
      reviewSearch: {
        params: [],
        offset: 0,
        limit: 3,
        columnName: '',
        order: ''
      }
    }
  },

  computed: {
    Company: function () {
      var user = JSON.parse(sessionStorage.getItem("currentUser"));
      return {
        id: user && user.company && user.company.id,
        label: user && user.company && user.company.label,
        name: user && user.company && user.company.name,
      };
    },
    UserId: function () {
      var user = JSON.parse(sessionStorage.getItem("currentUser"));
      return user && user.id;
    },
  },

  methods: {
    // 类型映射配置
    getTypeConfig: function (type) {
      var self = this;
      var configs = {
        djz: {
          load: function () { return self.loadDjz(); },
          ignore: function () { return self.ignoreDjz(); }
        },
        dsf: {
          load: function () { return self.loadDsf(); },
          ignore: function () { return self.ignoreDsf(); }
        },
        dfy: {
          load: function () { return self.loadDfy(); },
          ignore: function () { return self.ignoreDfy(); }
        },
        dzl: {
          load: function () { return self.loadDzl(); },
          ignore: function () { return self.ignoreDzl(); }
        },
        dsy: {
          load: function () { return self.loadDsy(); },
          ignore: function () { return self.ignoreDsy(); }
        },
        djyjc: {
          load: function () { return self.loadDjyjc(); },
          ignore: function () { return self.ignoreDjyjc(); }
        }
      };
      return configs[type];
    },

    // ========== 加载方法 ==========

    // 待就诊
    loadDjz: function () {
      var self = this;
      self.Totalsums = 0;
      self.TobeseenList = [];

      var params = [
        { columnName: "company_id", queryType: "=", value: this.Company.id },
        { columnName: "status", queryType: "=", value: "registrationStatus_0" },
        { columnName: "doctor_id", queryType: "=", value: this.UserId },
        { columnName: "overlook", queryType: "=", value: "0" },
      ];

      this.SearchPreModel.params = params;
      this.SearchPreModel.limit = 10;
      this.SearchPreModel.offset = 0;

      return listSchedulesPage(this.SearchPreModel).then(function (responseData) {
        if (responseData.code == 100) {
          self.Totalsums = responseData.data.total;
          var rows = responseData.data.rows || [];
          rows.forEach(function (element) {
            var birthday = element.patientId && element.patientId.birthday;
            if (birthday) {
              element.patientId.age = moment.duration(moment().diff(birthday)).years();
            } else {
              element.patientId.age = "--";
            }
          });
          self.TobeseenList = rows;
        }
        return responseData;
      });
    },

    // 待发药
    loadDfy: function () {
      var self = this;
      self.dispensingsum = 0;
      self.listdispensingcounts = [];

      this.PageRegistration.overlook = "0";
      this.PageRegistration.columnName = "return_date";
      this.PageRegistration.limit = 10;
      this.PageRegistration.offset = 0;
      this.PageRegistration.companyId = this.Company.id;

      return listdispensingPages(this.PageRegistration).then(function (responseData) {
        if (responseData.code == 100) {
          self.dispensingsum = responseData.data.total;
          var rows = responseData.data.rows || [];
          rows.forEach(function (item) {
            var datainfo = item.patient && item.patient.name || '';
            if (item.recipelInfoEvtList) {
              item.recipelInfoEvtList.forEach(function (itemevt) {
                var typeValue = itemevt.recipelInfo && itemevt.recipelInfo.recipelType && itemevt.recipelInfo.recipelType.value;
                if (typeValue == "recipelType_0" || typeValue == "recipelType_1") {
                  datainfo += " / " + (itemevt.recipelInfo.recipelType.name || '');
                  if (itemevt.recipelDetailEvtList) {
                    itemevt.recipelDetailEvtList.forEach(function (itemevt2) {
                      datainfo += " : " + (itemevt2.drugStuffId && itemevt2.drugStuffId.name || '');
                    });
                  }
                }
              });
            }
            self.$set(item, "displaycontent", datainfo);
          });
          self.listdispensingcounts = rows;
        }
        return responseData;
      });
    },

    // 待收费
    loadDsf: function () {
      var self = this;
      self.Patientchargesum = 0;
      self.PatientchargeList = [];

      this.patientQueryCondition.overlook = "0";
      this.patientQueryCondition.limit = 10;
      this.patientQueryCondition.offset = 0;
      this.patientQueryCondition.companyId = this.Company.id;
      this.patientQueryCondition.columnName = "reception_end_date";
      this.patientQueryCondition.order = "desc";
      this.patientQueryCondition.chargeStatus = 0;
      this.patientQueryCondition.updateDate = "2022-06-01 00:00:00";
      this.patientQueryCondition.status = "registrationStatus_1";

      return listdispensingPages(this.patientQueryCondition).then(function (responseData) {
        if (responseData.code == 100) {
          self.Patientchargesum = responseData.data.total;
          var rows = responseData.data.rows || [];
          rows.forEach(function (item) {
            var datainfo = item.patient && item.patient.name || '';
            if (item.recipelInfoEvtList) {
              item.recipelInfoEvtList.forEach(function (itemevt) {
                datainfo += " / " + (itemevt.recipelInfo && itemevt.recipelInfo.recipelType && itemevt.recipelInfo.recipelType.name || '');
                if (itemevt.recipelDetailEvtList) {
                  itemevt.recipelDetailEvtList.forEach(function (itemevt2) {
                    datainfo += " : " + (itemevt2.drugStuffId && itemevt2.drugStuffId.name || '');
                  });
                }
              });
            }
            self.$set(item, "displaycontent", datainfo);
          });
          self.PatientchargeList = rows;
        }
        return responseData;
      });
    },

    // 待治疗
    loadDzl: function () {
      var self = this;
      self.patientcuresum = 0;
      self.patientcurecounts = [];

      this.patientcure.overlook = "0";
      this.patientcure.limit = 10;
      this.patientcure.offset = 0;
      this.patientcure.companyId = this.Company.id;
      this.patientcure.columnName = "charge_date";
      this.patientcure.order = "desc";
      this.patientcure.chargeStatus = 1;
      this.patientcure.updateDate = "2022-06-01 00:00:00";
      this.patientcure.status = "registrationStatus_1";
      this.patientcure.cureType = 0;

      return listdispensingPages(this.patientcure).then(function (responseData) {
        if (responseData.code == 100) {
          self.patientcuresum = responseData.data.total;
          var rows = responseData.data.rows || [];
          rows.forEach(function (item) {
            var datainfo = item.patient && item.patient.name || '';
            if (item.recipelInfoEvtList) {
              item.recipelInfoEvtList.forEach(function (itemevt) {
                datainfo += " / " + (itemevt.recipelInfo && itemevt.recipelInfo.recipelType && itemevt.recipelInfo.recipelType.name || '');
                if (itemevt.recipelDetailEvtList) {
                  itemevt.recipelDetailEvtList.forEach(function (itemevt2) {
                    datainfo += " : " + (itemevt2.drugStuffId && itemevt2.drugStuffId.name || '');
                  });
                }
              });
            }
            self.$set(item, "displaycontent", datainfo);
          });
          self.patientcurecounts = rows;
        }
        return responseData;
      }).catch(function (error) {
        console.log(error);
        self.$message.error(error.message || '加载失败');
      });
    },

    // 待输液
    loadDsy: function () {
      var self = this;
      self.patientdsylistsums = 0;
      self.patientdsylistcounts = [];

      this.patientdsylist.overlook = "0";
      this.patientdsylist.limit = 10;
      this.patientdsylist.offset = 0;
      this.patientdsylist.companyId = this.Company.id;
      this.patientdsylist.columnName = "dispensing_date";
      this.patientdsylist.order = "desc";
      this.patientdsylist.updateDate = "2022-06-01 00:00:00";
      this.patientdsylist.status = "registrationStatus_1";
      this.patientdsylist.infuseType = 0;

      return listdispensingPages(this.patientdsylist).then(function (responseData) {
        if (responseData.code == 100) {
          self.patientdsylistsums = responseData.data.total;
          var rows = responseData.data.rows || [];
          rows.forEach(function (item) {
            var datainfo = item.patient && item.patient.name || '';
            if (item.recipelInfoEvtList) {
              item.recipelInfoEvtList.forEach(function (itemevt) {
                datainfo += " / " + (itemevt.recipelInfo && itemevt.recipelInfo.recipelType && itemevt.recipelInfo.recipelType.name || '');
                if (itemevt.recipelDetailEvtList) {
                  itemevt.recipelDetailEvtList.forEach(function (itemevt2) {
                    datainfo += " : " + (itemevt2.drugStuffId && itemevt2.drugStuffId.name || '');
                  });
                }
              });
            }
            self.$set(item, "displaycontent", datainfo);
          });
          self.patientdsylistcounts = rows;
        }
        return responseData;
      }).catch(function (error) {
        console.log(error);
        self.$message.error(error.message || '加载失败');
      });
    },

    // 待检验检查
    loadDjyjc: function () {
      var self = this;
      self.jyjctotal = 0;
      self.jyjclistcounts = [];

      this.searchjyjc.offset = 0;
      this.searchjyjc.limit = 10;

      return listInspectionCheckPage(this.searchjyjc).then(function (responseData) {
        if (responseData.code == 100) {
          self.jyjctotal = responseData.data.total;
          self.jyjclistcounts = responseData.data.rows || [];
        }
        return responseData;
      }).catch(function (error) {
        self.outputError(error);
      });
    },

    // ========== 并发加载所有 ==========
    loadlist: function () {
      var self = this;
      var loadingInstance = Loading.service({ fullscreen: true });

      // 所有加载请求
      var requests = [
        this.loadDjz(),
        this.loadDfy(),
        this.loadDsf(),
        this.loadDzl(),
        this.loadDsy(),
        this.loadDjyjc()
      ];

      Promise.all(requests).then(function () {
        setTimeout(function () {
          loadingInstance.close();
        }, 500);
      }).catch(function (error) {
        console.error('加载失败:', error);
        loadingInstance.close();
      });
    },

    // ========== 刷新单个 ==========
    typeclickload: function (type) {
      var config = this.getTypeConfig(type);
      if (config && config.load) {
        config.load();
      }
    },

    // ========== 忽略单条 ==========
    btnoverlook: function (id, type) {
      var self = this;
      updateoverlockids(id).then(function (responseData) {
        self.$message.success(responseData.msg);
        self.typeclickload(type);
      });
    },

    // ========== 忽略全部 ==========
    btnoverlookidlist: function (type) {
      var config = this.getTypeConfig(type);
      if (config && config.ignore) {
        config.ignore();
      }
    },

    // 忽略全部 - 待就诊
    ignoreDjz: function () {
      var self = this;
      this.SearchPreModel.companyId = this.Company.id;
      this.SearchPreModel.doctorid = this.UserId;
      this.SearchPreModel.status = "registrationStatus_0";
      this.SearchPreModel.overlook = "0";
      updateoverlockidlist(this.SearchPreModel).then(function (responseData) {
        self.typeclickload('djz');
        self.$message.success(responseData.msg);
      });
    },

    // 忽略全部 - 待收费
    ignoreDsf: function () {
      var self = this;
      this.patientQueryCondition.overlook = "0";
      this.patientQueryCondition.limit = 10;
      this.patientQueryCondition.offset = 0;
      this.patientQueryCondition.companyId = this.Company.id;
      this.patientQueryCondition.columnName = "reception_end_date";
      this.patientQueryCondition.order = "desc";
      this.patientQueryCondition.chargeStatus = 0;
      this.patientQueryCondition.updateDate = "2022-06-01 00:00:00";
      this.patientQueryCondition.status = "registrationStatus_1";
      updateoverlockidlist(this.patientQueryCondition).then(function (responseData) {
        self.typeclickload('dsf');
        self.$message.success(responseData.msg);
      });
    },

    // 忽略全部 - 待发药
    ignoreDfy: function () {
      var self = this;
      this.PageRegistration.overlook = "0";
      this.PageRegistration.columnName = "return_date";
      this.PageRegistration.limit = 10;
      this.PageRegistration.offset = 0;
      this.PageRegistration.companyId = this.Company.id;
      updateoverlockidlist(this.PageRegistration).then(function (responseData) {
        self.typeclickload('dfy');
        self.$message.success(responseData.msg);
      });
    },

    // 忽略全部 - 待治疗
    ignoreDzl: function () {
      var self = this;
      this.patientcure.overlook = "0";
      this.patientcure.limit = 10;
      this.patientcure.offset = 0;
      this.patientcure.companyId = this.Company.id;
      this.patientcure.columnName = "charge_date";
      this.patientcure.order = "desc";
      this.patientcure.chargeStatus = 1;
      this.patientcure.updateDate = "2022-06-01 00:00:00";
      this.patientcure.status = "registrationStatus_1";
      this.patientcure.cureType = 0;
      updateoverlockidlist(this.patientcure).then(function (responseData) {
        self.typeclickload('dzl');
        self.$message.success(responseData.msg);
      });
    },

    // 忽略全部 - 待输液
    ignoreDsy: function () {
      var self = this;
      this.patientdsylist.overlook = "0";
      this.patientdsylist.limit = 10;
      this.patientdsylist.offset = 0;
      this.patientdsylist.companyId = this.Company.id;
      this.patientdsylist.columnName = "dispensing_date";
      this.patientdsylist.order = "desc";
      this.patientdsylist.updateDate = "2022-06-01 00:00:00";
      this.patientdsylist.status = "registrationStatus_1";
      this.patientdsylist.infuseType = 0;
      updateoverlockidlist(this.patientdsylist).then(function (responseData) {
        self.typeclickload('dsy');
        self.$message.success(responseData.msg);
      });
    },

    // 忽略全部 - 待检验检查
    ignoreDjyjc: function () {
      var self = this;
      this.searchjyjc.companyId = this.Company.id;
      this.searchjyjc.status = "0";
      this.searchjyjc.overlook = "0";
      updateoverlockidlist(this.searchjyjc).then(function (responseData) {
        self.typeclickload('djyjc');
        self.$message.success(responseData.msg);
      });
    },

    // ========== 跳转 ==========
    skiptobeseen: function (itemid, type) {
      var routeMap = {
        djz: '/medicalOutpatientRecord',
        dfy: '/supplierStock',
        dsf: '/tollInfo',
        dzl: '/cureManagement',
        dsy: '/infusion',
        djyjc: '/inspectionCheck'
      };
      var route = routeMap[type];
      if (route) {
        this.$router.push({ path: route, params: { id: itemid } });
      }
    },

    // ========== 打开弹窗 ==========
    onCreatePatient: function (types) {
      this.$refs.workbenchForm.$emit('openAddworkbenchDialog', types);
    },

    // ========== 处方审查 ==========
    initReviewList: function () {
      var self = this;
      this.reviewSearch.params = [
        { columnName: 'recipelInfo.company_id', queryType: '=', value: currentUser.company.id },
        { columnName: 'registration.doctor_id', queryType: '=', value: currentUser.id },
        { columnName: 'review_result', queryType: '=', value: 0 },
        { columnName: 'recipelInfo.not_show', queryType: '=', value: 1 }
      ];

      listRecipelInfoReviewPage(this.reviewSearch).then(function (responseData) {
        if (responseData.code == 100) {
          self.reviewTotal = responseData.data.total;
          var list = [];
          if (responseData.data.rows) {
            responseData.data.rows.forEach(function (row) {
              list.push({
                recipelInfoId: row.recipelInfo.id,
                content: row.recipelInfo.registration.patientId.name + "/" + row.recipelInfo.recipelType.name + "/" + row.reviewContent
              });
            });
          }
          self.reviewList = list;
        } else {
          self.showMessage(responseData);
        }
        self.resetLoad();
      }).catch(function (error) {
        self.outputError(error);
      });
    },

    onClearAll: function (data) {
      var self = this;
      data.forEach(function (item) {
        self.updateNotShowById(item.recipelInfoId, "all");
      });
      this.showMessage({ type: 'success', msg: '操作成功' });
    },

    onOpenAll: function () {
      console.log("onOpenAll");
    },

    onClickContent: function (item) {
      console.log(item, "onClickContent");
      this.$router.push({ path: '/recipelInfoReviewResult', params: {} });
    },

    onClickOne: function (item) {
      console.log(item, "onClickOne");
      this.$router.push({ path: '/recipelInfoReviewResult', params: {} });
    },

    onClickTwo: function (item) {
      console.log(item, "onClickTwo");
      this.updateNotShowById(item.recipelInfoId, "single");
    },

    updateNotShowById: function (recipelInfoId, type) {
      var self = this;
      updateNotShowById(recipelInfoId).then(function (responseData) {
        if (responseData.code == 100) {
          self.initReviewList();
          if ("all" != type) {
            self.showMessage({ type: 'success', msg: '操作成功' });
          }
        }
      });
    }
  },

  mounted: function () {
    this.loadlist();
    this.initReviewList();
  }
}
</script>

<style lang="scss">
.rowcrad .el-card {
  height: 160px;
}

.el-col {
  margin-top: 10px;
}
</style>

<style>
.el-col {
  border-radius: 4px;
}

.grid-content {
  border-radius: 4px;
  min-height: 36px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.el-card__header {
  padding: 8px;
}

.el-card__body {
  height: auto;
  padding: 0 10px 0 10px;
}

.text {
  padding: 10px 0 0 0;
  font-size: 14px;
  width: 100%;
  text-align: left;
  clear: both;
}

.cartitle {
  font-weight: bold;
  color: #018cb7;
}

.cara1 {
  display: inline-block;
  text-align: left;
  width: calc(100% - 90px);
  overflow: hidden;
  word-break: keep-all;
  white-space: nowrap;
  text-overflow: ellipsis;
  text-decoration: none;
  color: #333;
  clear: both;
}

.cara1:hover {
  color: #018cb7;
  cursor: pointer;
}

.cara2 {
  display: inline;
  float: right;
  clear: both;
}

.cara2 a {
  text-decoration: none;
  color: #333;
}

.cara2 a:hover {
  color: #018cb7;
  cursor: pointer;
}

.badgeitem {
  height: auto;
  line-height: 0%;
}
</style>
