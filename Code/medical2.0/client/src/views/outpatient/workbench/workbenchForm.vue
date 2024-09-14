<template>
  <el-dialog
    :title="dialogProps.title"
    :visible.sync="dialogProps.visible"
    :close-on-click-modal="false"
    width="35%"
    @open="onDialogOpen()"
    v-loading="loading"
  >
    <el-form
      :model="bizFormModel"
      :rules="formRules"
      ref="workbenchForm"
      label-width="120px"
      label-position="right"
      class="edit-form"
    >
      <div class="tab-item" v-show="tabIndex == '1'">
        <el-table
    :data="tableData"
    style="width: 100%">
    <el-table-column
             label="序号"
              type="index"
              :index="indexMethod"
              align="center">
            </el-table-column>
    <el-table-column
      prop="id"
      label="ID"
      :show-overflow-tooltip="true"
      v-if="false"
      min-width="20%">
    </el-table-column>
    <el-table-column
      prop="name"
      label="名称"
      :show-overflow-tooltip="true" 
      min-width="78%">
    </el-table-column>
    <el-table-column
      label="操作"
      min-width="20%"
      >
      <template slot-scope="scope">
        <a @click="handleClick(titletype,scope.row.id)" type="text" size="small" class="fontcolor">就诊</a>
        <a type="text" size="small" class="fontcolor"  @click="btnoverlook(scope.row.id,titletype)" >忽略</a>
      </template>
    </el-table-column>
    
  </el-table>
      </div>
      <el-pagination
                small
                background
                @current-change="handleCurrentPreparePatientChange"
                :page-size="fymodel.limit"
                layout="prev, pager, next"
                :total="totallistcounts"
              >
              </el-pagination>
    </el-form>
    <span slot='footer' class='dialog-footer'>
      <el-button :disabled="flage" type='primary' :plain='true' @click='onDialogClose()'>确 定</el-button>
      <el-button :plain='true' type="primary" @click='onDialogClose()'>取 消</el-button>
    </span>    
  </el-dialog>
</template>
<script>
import BaseUI from "@/views/components/baseUI";
import OperationIcon from "@/components/OperationIcon";
import VDistpicker from 'v-distpicker';
import {
  listSchedulesPage,
  listdispensingPages,
  listPatientcharge,
  updateoverlockids,
} from "@/api/workbench/Schedules";
import moment from "moment";
import { listInspectionCheckPage } from '@/api/cure/inspectionCheck'
export default {
  extends: BaseUI,
  name: "workbench-form",
  components: {
    OperationIcon,
    VDistpicker
  },  
   props:['closeValue'],
  data() {
    return {
tableData: [
  {
    id:'',
    name: '',
 },
 ],
fymodel: {
        columnName: "",
        limit: 10,
        offset: 0,
        order: "",
      },
      totallistcounts:0,
      currentPage:1,
      dialogProps: {
        visible: false,
        action: '',
        title: '',
      },
      //待执行类型
     types:"",

     titletype:"",

//今日待就诊患者列表
      TobeseenList: [],
      //今日待就诊患者总数
      Totalsums: "",
      //搜索患者信息
      SearchPatientInfo: "",
      //就诊搜索条件
      SearchPreModel: {
        columnName: "",
        limit: 1000,
        offset: 0,
        order: "",
        params:[],
      },


       //待发药新的传参实体类
      PageRegistration: {
        columnName: "",
        limit: 0,
        offset: 0,
        order: "desc",
        companyId: "",
        recipeStatus: "0",
        updateDate: "2022-06-01 00:00:00",
        status: "registrationStatus_1",
        chargeStatus: 1,
        dispensionStatus: 0,
        recipelType:'recipelType_3',
        patientName:'',
        patientCode:'',
        overlook:'',
      },
      //待发药数据
      listdispensingcounts:[],
      //待发药总人数
      dispensingsum:0,

      //待收费
       //新的传参实体类
      patientQueryCondition: {
        columnName: "",
        limit: 0,
        offset: 0,
        order: "",
        companyId: "",
        recipeStatus: "0",
        updateDate: "2022-06-01 00:00:00",
        status: "registrationStatus_1",
        chargeStatus: "",
        dispensionStatus:3,
        patientName:null,
        patientCode:null,
        overlook:'',
      },
      //待收费数据
      PatientchargeList:[],
      Patientchargesum:0,
//待治疗
      patientcure: {
        columnName: "",
        limit: 0,
        offset: 0,
        order: "",
        companyId: "",
        recipeStatus: "0",
        updateDate: "2022-06-01 00:00:00",
        status: "registrationStatus_1",
        chargeStatus: "",
        dispensionStatus:3,
        patientName:null,
        patientCode:null,
        recipelType:'recipelType_10',
        cureType:0,
        overlook:'',
      },
      patientcurecounts:[],
      patientcuresum:0,
//待输液
     patientdsylist: {
        columnName: "",
        limit: 0,
        offset: 0,
        order: "",
        companyId: "",
        recipeStatus: "0",
        updateDate: "2022-06-01 00:00:00",
        status: "registrationStatus_1",
        chargeStatus: 1,
        dispensionStatus:1,
        patientName:null,
        patientCode:null,
        recipelType:'recipelType_11',
        infuseType:0,
        overlook:"",
      },
      patientdsylistcounts:[],
      patientdsylistsums:0,

      //待检验检查
      searchjyjc: {
        params: [
          {columnName: 'company_id', queryType: '=', value: currentUser.company.id},
          {columnName: 'status', queryType: '=', value: "0"},
          {columnName:'registration.overlook', queryType: '=', value: "0"},
          ],    
        offset: 0,
        limit: 1000,
		columnName: '',      // 排序字段名
        order: ''            // 排序
      },
      jyjclistcounts:[],
      jyjctotal:0,


      PreparePatientTotal: 0,

    };
  },

  methods: {
    //关闭弹窗
   onDialogClose() {
    // debugger
     this.$emit('typeclick',this.titletype);
      this.dialogProps.visible = false;
      
    },
    
    handleCurrentPreparePatientChange(pageNum) {
      this.fymodel.offset = (pageNum - 1) * this.fymodel.limit;
      this.Getpatenlist();
    },
    indexMethod(index){
       return (this.currentPage-1)*this.fymodel.limit+index +1;
    },
    initFormModel(This) {
      return {
        company: {
          // 诊所id
          id: currentUser.company.id,
          name: null,
        },
        withPatientNexus: {
          // 与患者关系
          value: null,
          name: null,
        },
      };
    },
    onDialogOpen() {
      this.$nextTick(() => {
        this.$refs["workbenchForm"].clearValidate();
      });
    },
    initOptions(This) {
      let division_search = {
        params: [
          {
            columnName: "parten_id",
            queryType: "=",
            value: "0",
          },
          {
            columnName: "level",
            queryType: "=",
            value: "1",
          }
        ],
      }

      let gender_search = {
        params: [
          {
            columnName: "dict_type_id",
            queryType: "=",
            value: "1008489176147648522",
          },
        ],
      };
     
    },

Getpatenlist(){
  switch(this.types){
    case "djz":
this.GetPreparePatientList();
      break;
      case "dsf":
this.selectPatientchargeList();
      break;
      case "dfy":
this.GetDispensingTable();
      break;
      case "dzl":
this.selectcurePatientList();
      break;
      case "dsy":
this.selectpatientdsylist();
      break;
      case "djyjc":
this.selectpatientdjyjclist();
      break;
  }
},

//获取待就诊患者列表
    GetPreparePatientList() {
        this.tableData=[];
      let params = [
        {
          columnName: "company_id",
          queryType: "=",
          value: currentUser.company.id,
        },
        {
          columnName: "status",
          queryType: "=",
          value: "",
        },
        {
          columnName: "doctor_id",
          queryType: "=",
          value: currentUser.id,
        },
        {
          columnName: "overlook",
          queryType: "=",
          value:"0",
        },
      ];
      this.SearchPreModel.offset=this.fymodel.offset;
      this.SearchPreModel.limit=this.fymodel.limit;
      this.SearchPreModel.params = params;
      this.SearchPreModel.params[1].value = "registrationStatus_0";//待就诊
      listSchedulesPage(this.SearchPreModel).then((responseData) => {
        if (responseData.code == 100) {
          
          this.Totalsums = responseData.data.total;
          this.totallistcounts=responseData.data.total;
          if (responseData.data.rows) {
            this.TobeseenList=responseData.data.rows
            responseData.data.rows.forEach((element) => {
               let birthday = element.patientId.birthday;
              if (birthday) {
                const duration = moment.duration(moment().diff(birthday));
                element.patientId.birthdayText = duration.years();
              } else {
                element.patientId.birthdayText = "--";
              }
              this.tableData.push({
                id:element.id,
                name:element.patientId.name+" / "+element.patientId.birthdayText,
              });
            });
          }
          this.PreparePatientList = responseData.data.rows;
        }
      });
    },
//获取待发药列表
GetDispensingTable() {
  this.tableData=[];
  this.PageRegistration.overlook="0",
 this.PageRegistration.columnName="return_date",
 this.PageRegistration.limit=this.fymodel.limit,
 this.PageRegistration.offset=this.fymodel.offset,
      this.PageRegistration.companyId=currentUser.company.id,
      listdispensingPages(this.PageRegistration).then((responseData) => {
        if (responseData.code == 100) {
          this.dispensingsum=responseData.data.total;
          this.totallistcounts=responseData.data.total;
          console.info("count:"+responseData.data.length);
          responseData.data.rows.forEach((item)=>{
            let datainfo=item.patient.name;
            item.recipelInfoEvtList.forEach((itemevt)=>{
              if(itemevt.recipelInfo.recipelType.value=="recipelType_0" || itemevt.recipelInfo.recipelType.value=="recipelType_1")
              {
                datainfo+=" / " + itemevt.recipelInfo.recipelType.name;
                //  + " : "+ itemevt.recipelDetailEvtList.drugStuffId.name;
                itemevt.recipelDetailEvtList.forEach((itemevt2)=>{
                   datainfo+=" : "+ itemevt2.drugStuffId.name;
                })
              }
            })
            this.$set(item,"displaycontent",datainfo);
            //this.listdispensingcounts.push(datainfo);
            this.tableData.push({
              id:item.id,
                name:datainfo,
              });
          })
          this.listdispensingcounts=responseData.data,rows;
        }
      });
    },

    //待收费
selectPatientchargeList(){
  this.tableData=[];
  this.patientQueryCondition.overlook="0",
this.patientQueryCondition.limit = this.fymodel.limit;
      this.patientQueryCondition.companyId = currentUser.company.id,
      /*待收费*/
      this.isOnlyRead = false;
      this.patientQueryCondition.offset =this.fymodel.offset;
      this.patientList = [];
      this.patientQueryCondition.columnName = "reception_end_date";
      this.patientQueryCondition.order = "desc";
      this.patientQueryCondition.chargeStatus = 0;
      this.patientQueryCondition.updateDate = "2022-06-01 00:00:00";
      this.patientQueryCondition.status = "registrationStatus_1";

        listdispensingPages(this.patientQueryCondition).then((responseData) => {
        if (responseData.code == 100) {
          this.Patientchargesum=responseData.data.total;
          this.totallistcounts=responseData.data.total;
          responseData.data.rows.forEach((item)=>{
            let datainfo=item.patient.name;
            item.recipelInfoEvtList.forEach((itemevt)=>{
                datainfo+=" / " + itemevt.recipelInfo.recipelType.name;
                //  + " : "+ itemevt.recipelDetailEvtList.drugStuffId.name;
                itemevt.recipelDetailEvtList.forEach((itemevt2)=>{
                   datainfo+=" : "+ itemevt2.drugStuffId.name;
                })
            })
            this.$set(item,"displaycontent",datainfo);
            //this.listdispensingcounts.push(datainfo);
            this.tableData.push({
              id:item.id,
                name:datainfo,
              });
          })
          this.PatientchargeList=responseData.data.rows;
        }

       });

  },

//待治疗
selectcurePatientList(){
  this.tableData=[];
  this.patientcure.overlook="0",
  this.patientcure.limit = this.fymodel.limit;
      this.patientcure.companyId = currentUser.company.id,
      /*待完成*/
      this.isOnlyRead = false;
      this.patientcure.offset =this.fymodel.offset;
      this.patientcure.columnName = "charge_date";
      this.patientcure.order = "desc";
      this.patientcure.chargeStatus = 1;
      this.patientcure.updateDate = "2022-06-01 00:00:00";
      this.patientcure.status = "registrationStatus_1";
      this.patientcure.cureType=0;

      listdispensingPages(this.patientcure).then((responseData) => {
        if (responseData.code == 100) {
          this.patientcuresum=responseData.data.total;
          this.totallistcounts=responseData.data.total;
          responseData.data.rows.forEach((item)=>{
            let datainfo=item.patient.name;
            item.recipelInfoEvtList.forEach((itemevt)=>{
                datainfo+=" / " + itemevt.recipelInfo.recipelType.name;
                //  + " : "+ itemevt.recipelDetailEvtList.drugStuffId.name;
                itemevt.recipelDetailEvtList.forEach((itemevt2)=>{
                   datainfo+=" : "+ itemevt2.drugStuffId.name;
                })
            })
            this.$set(item,"displaycontent",datainfo);
            //this.listdispensingcounts.push(datainfo);
            this.tableData.push({
              id:item.id,
                name:datainfo,
              });
          })
          this.patientcurecounts=responseData.data.rows;
        }

       })
        .catch((error) => {
          console.log(error);
          this.$message.error(error);
        });
},
//待输液
selectpatientdsylist(){
  this.tableData=[];
  this.patientdsylistsums=0;
  this.patientdsylistcounts=[];
  this.patientdsylist.overlook="0",
this.patientdsylist.limit = this.fymodel.limit;
      this.patientdsylist.companyId = currentUser.company.id;
      /*待收费*/
      this.isOnlyRead = false;
      this.patientdsylist.offset =this.fymodel.offset;
      this.patientList = [];
      this.patientdsylist.columnName = "dispensing_date";
      this.patientdsylist.order = "desc";
      this.patientdsylist.updateDate = "2022-06-01 00:00:00";
      this.patientdsylist.status = "registrationStatus_1";
      this.patientdsylist.infuseType=0;
      this.patientdsylist.patientName='';
      this.patientdsylist.patientCode='';
      listdispensingPages(this.patientdsylist).then((responseData) => {
        if (responseData.code == 100) {
          this.patientdsylistsums=responseData.data.total;
          this.totallistcounts=responseData.data.total;
          responseData.data.rows.forEach((item)=>{
            let datainfo=item.patient.name;
            item.recipelInfoEvtList.forEach((itemevt)=>{
                datainfo+=" / " + itemevt.recipelInfo.recipelType.name;
                //  + " : "+ itemevt.recipelDetailEvtList.drugStuffId.name;
                itemevt.recipelDetailEvtList.forEach((itemevt2)=>{
                   datainfo+=" : "+ itemevt2.drugStuffId.name;
                })
            })
            this.$set(item,"displaycontent",datainfo);
            //this.listdispensingcounts.push(datainfo);
            this.tableData.push({
              id:item.id,
                name:datainfo,
              });
          })
          this.patientdsylistcounts=responseData.data.rows;
        }

       })
        .catch((error) => {
          console.log(error);
          this.$message.error(error);
        });
},

//待检验检查
selectpatientdjyjclist(){
  this.tableData=[];
  this.searchjyjc.offset=this.fymodel.offset;
  this.searchjyjc.limit=this.fymodel.limit;
  listInspectionCheckPage(this.searchjyjc).then(responseData => {
    this.totallistcounts=responseData.data.total;
        if(responseData.code == 100) {
          responseData.data.rows.forEach((item)=>{
            this.tableData.push({
              id:item.registration.id,
                name:item.patient.name + " / " + item.patient.age + " / " + item.name,
              });
          this.jyjclistcounts=responseData.data.rows;
          this.jyjctotal=responseData.data.total;
          })
          
        } else {
        }
        this.resetLoad()
      }).catch(error => {
        this.outputError(error)
      })
},

//跳转路由
handleClick(type,itemid){
type=this.types
switch(type){
  case "djz":
this.$router.push({ path: '/medicalOutpatientRecord', params: { id: itemid }})
break;
case "dfy":
  this.$router.push({ path: '/supplierStock', params: { id: itemid }})
  break;
  case "dsf":
  this.$router.push({ path: '/tollInfo', params: { id: itemid }})
  break;
    case "dzl":
  this.$router.push({ path: '/cureManagement', params: { id: itemid }})
  break;
  case "dsy":
  this.$router.push({ path: '/infusion', params: { id: itemid }})
  break;
  case "djyjc":
  this.$router.push({ path: '/inspectionCheck', params: { id: itemid }})
  break;
}
},
//获取Id进行忽略单条数据
    btnoverlook(values,types){
      this.tableData=[];
      let strRecipelInfoIds=values;
      updateoverlockids(strRecipelInfoIds).then((responseData) =>{
        this.$message.success(responseData.msg)
      });
       switch(types){
  case "djz":
this.GetPreparePatientList();
break;
case "dfy":
  this.GetDispensingTable();
  break;
  case "dsf":
  this.selectPatientchargeList();
  break;
  case "dzl":
this.selectcurePatientList();
  break;
  case "dsy":
this.selectpatientdsylist();
  break;
  case "djyjc":
this.selectpatientdjyjclist();
  break;
}
    },
  },
  mounted: function() {
    this.$nextTick(() => {
      this.$on('openAddworkbenchDialog', function(types) {
        let titles="";
        this.titletype=types;
        switch(types){
          case "djz":
          titles="待就诊"
          break;
          case "dsf":
            titles="待收费"
          break;
          case "dfy":
          titles="待发药"
          break;
          case "dzl":
            titles="待治疗"
          break;
          case "dsy":
            titles="待输液"
          break;
          case "djyjc":
            titles="待检验检查"
          break;
        }
        this.types=types;
        this.dialogProps.action = 'add'
        this.dialogProps.title = titles+'全部信息'
        this.bizFormModel = this.initFormModel(this.user)
        this.initOptions(this.bizFormModel)
        this.tabIndex = '1'
        this.dialogProps.visible = true
        this.province=''
        this.city=''
        this.area=''
        this.Getpatenlist()
      })
    });
  }  
}
</script>
<style>
.el-dialog .el-dialog__body{
  padding:0 10px 0 10px;
}
.fontcolor:hover{
   color:#018cb7;
   cursor: pointer;
}
</style>
