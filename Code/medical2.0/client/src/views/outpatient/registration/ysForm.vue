<template>
  <el-dialog
    class="ysForm"
    :title="dialogProps.title"
    :visible.sync="dialogProps.visible"
    :close-on-click-modal="false"
    width="50%"
    @open="onDialogOpen()"
    v-loading="loading"
  >
    <el-form
      :model="queryModel"
      :rules="formRules"
      ref="ysForm"
      label-width="120px"
      label-position="right"
      style="margintop: 10px"
      class="edit-form"
    >
      <el-row>
        <el-col :span="24 / 2">
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
      </el-row>
    </el-form>
    <div slot="footer">
      <el-button @click="onDialogclose">关  闭</el-button>
      <el-button type="primary" @click="buttoninser"
        >确  认</el-button
      >
    </div>
  </el-dialog>
</template>

<script>
import BaseUI from "@/views/components/baseUI";
import { listDictItemAll } from "@/api/sys/dictItem";
import { saveRegistration,listDoctorsAll,listDoctorsAllnew,registationupdatenew } from "@/api/outpatient/registration";
export default {
  extends: BaseUI,
  name: "ys-form",
  data() {
    return {
      dialogProps: {
        visible: false,
        action: "",
        title: "",
        source: {
          name: "现场",
          value: "registrationSource_0",
        },
      },
      queryTypes: {
        department_id: "=",
        patient_id: "=",
        doctor_id: "=",
      },
      queryModel: {
        doctor: {
          // 医生
          id: null,
          name: "",
        }
      },
      doctor_List: [], // 医生
      status_List: [], // 状态

      insetModel:{
         registration:{
        id:"",
        company:{
          id:currentUser.company.id,
          name:null,
        },
        doctor:{id:null,name:null},
        patientId:{id:null,name:null},
        status:{name:"待就诊",value:"registrationStatus_0"},
         },
      },
    };
  },
  methods: {
    onDialogOpen() {
      this.$nextTick(() => {
        this.$refs["ysForm"].clearValidate();
      });
    },
    arrlistcreat() {
      this.queryModel = {
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
        cource: "",
      };
    },
    onDialogclose() {
      this.$emit('typeclick');
      this.dialogProps.visible = false;
      this.arrlistcreat();
    },
    initys() {
      //挂号状态
      let status_search = {
        params: [
          {
            columnName: "dict_type_id",
            queryType: "=",
            value: "1008898177293385773",
          },
        ],
      };
      // 字段对应表上filter条件
      status_search.params.push.apply(status_search.params, []);
      // 数据权限: 字典项sys_dict_item
      this.pushDataPermissions(
        status_search.params,
        this.$route.meta.routerId,
        "4005"
      );
      this.status_List.splice(0, this.status_List.length);
      listDictItemAll(status_search).then((responseData) => {
        this.status_List = responseData.data;
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

      this.doctor_List.splice(0, this.doctor_List.length);
      // listDoctorsAll(doctor_search).then((responseData) => {
      //   this.doctor_List = responseData.data;
      // });
      listDoctorsAllnew().then((responseData) => {
        //console.log(responseData,'医生');
        this.doctor_List = responseData.data;
      })
    },
    buttoninser(){
      if(this.queryModel.doctor.id==null&&this.queryModel.doctor.name==""){
         this.$message({
                showClose: true,
                message:'请选择医生',
                type: 'error'
             });
             return;
      }
      this.insetModel.registration.doctor.id=this.queryModel.doctor.id;
      this.insetModel.registration.doctor.name=this.queryModel.doctor.name;
      registationupdatenew(this.insetModel)
        .then((responseData) => {
          if (responseData.code == 100) {
            this.$emit('typeclick',"");
            this.arrlistcreat();
            this.dialogProps.visible = false;
           
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
  mounted: async function () {
    this.$nextTick(() => {
      this.$on("openViewysDialog", function (item,types) {
        //debugger;
        this.arrlistcreat();
        this.dialogProps.action = "view";
        this.dialogProps.title = "分诊";

        this.insetModel.registration.id=item.id;
        this.insetModel.registration.patientId.id=item.patientId.id;
        this.insetModel.registration.patientId.name=item.patientId.name;
        this.initys();
        console.log(this.queryModel);
        this.dialogProps.visible = true;
      });
    });
  },
};
</script>
  