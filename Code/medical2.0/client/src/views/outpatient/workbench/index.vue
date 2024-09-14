
<template>
  <div>
    <el-row :gutter="10" class="rowcrad">
      <el-col  :lg="6" :xs="12" :sm="6" v-if="TobeseenList.length>0">
        <el-card class="box-card" shadow="always">
          <workbench-form ref='workbenchForm' @typeclick="typeclickload"></workbench-form>
          <div class="card-header">
            <span class="cartitle" >待就诊</span>
            <div>
              <el-button style=" padding: 3px 0" type="text" @click="btnoverlookidlist('djz')">忽略全部</el-button>
            <el-badge :value="Totalsums<10?Totalsums:'10+'" class="badgeitem" v-show="Totalsums>0"  @click.native="onCreatePatient('djz')"></el-badge>
            </div>
          </div>
        <div v-for="(item,index) in TobeseenList.slice(0, 3)" :key="index" class="text" >
          <a  @click="skiptobeseen(item.patientId.id,'djz')"  class="cara1">{{ index + 1 }}. {{ item.patientId.name }} / {{ item.patientId.gender.name }} / {{ item.patientId.birthdayText }}岁</a>
          <span class="cara2"><a @click="skiptobeseen(item.patientId.id,'djz')" >就诊</a>
          <a @click="btnoverlook(item.id,'djz')"  style="margin-left:5px;">忽略</a></span></div>
      </el-card>
      </el-col>
      <el-col  :lg="6" :xs="12" :sm="6" v-if="PatientchargeList.length>0">
        <el-card class="box-card" shadow="always">
        <workbench-form ref='workbenchForm' @typeclick="typeclickload"></workbench-form>
          <div class="card-header">
            <span class="cartitle">待收费</span>
            <div>
              <el-button style=" padding: 3px 0" type="text" @click="btnoverlookidlist('dsf')">忽略全部</el-button>
            <el-badge :value="Patientchargesum<10?Patientchargesum:'10+'" class="badgeitem"  @click.native="onCreatePatient('dsf')"></el-badge>
            </div>
          </div>
        <div v-for="(item,index) in PatientchargeList.slice(0, 3)" :key="index"  class="text" >
          <a @click="skiptobeseen(item.patient.id,'dsf')"  class="cara1">{{ item.displaycontent }}</a><span class="cara2"><a  @click="skiptobeseen(item.patient.id,'dsf')" >查看</a>
          <a @click="btnoverlook(item.id,'dsf')" style="margin-left:5px;">忽略</a></span></div>
      </el-card></el-col>
      <el-col  :lg="6" :xs="12" :sm="6" v-if="listdispensingcounts.length>0" >
        <el-card class="box-card" shadow="always">
          <workbench-form ref='workbenchForm' @typeclick="typeclickload"></workbench-form>
          <div class="card-header">
            <span class="cartitle">待发药</span>
            <div>
              <el-button style=" padding: 3px 0" type="text" @click="btnoverlookidlist('dfy')">忽略全部</el-button>
            <el-badge :value="dispensingsum<10?dispensingsum:'10+'"  class="badgeitem"  @click.native="onCreatePatient('dfy')"></el-badge>
            </div>
          </div>
        <div v-for="(item,index) in listdispensingcounts.slice(0, 3)" :key="index"  class="text" >
          <a  @click="skiptobeseen(item.patient.id,'dfy')"  class="cara1">{{ item.displaycontent }}</a><span class="cara2"><a @click="skiptobeseen(item.patient.id,'dfy')"  >查看</a>
          <a  @click="btnoverlook(item.id,'dfy')"  style="margin-left:5px;">忽略</a></span></div>
      </el-card></el-col>
        <el-col  :lg="6" :xs="12" :sm="6"  v-if="patientcurecounts.length>0" ><el-card class="box-card" shadow="always">
        <workbench-form ref='workbenchForm' @typeclick="typeclickload"></workbench-form>
          <div class="card-header">
            <span class="cartitle">待治疗</span>
            <div>
              <el-button style=" padding: 3px 0" type="text" @click="btnoverlookidlist('dzl')">忽略全部</el-button>
            <el-badge :value="patientcuresum<10?patientcuresum:'10+'" class="badgeitem"  @click.native="onCreatePatient('dzl')"></el-badge>
            </div>
          </div>
        <div v-for="(item,index) in patientcurecounts.slice(0, 3)" :key="index"  class="text" >
          <a @click="skiptobeseen(item.patient.id,'dzl')"  class="cara1">{{ item.displaycontent }}</a><span class="cara2">
            <a  @click="skiptobeseen(item.patient.id,'dzl')">就诊</a>
            <a  @click="btnoverlook(item.id,'dzl')"  style="margin-left:5px;">忽略</a></span></div>
      </el-card></el-col>
      <el-col  :lg="6" :xs="12" :sm="6" v-if="patientdsylistcounts.length>0" ><el-card class="box-card" shadow="always">
        <workbench-form ref='workbenchForm' @typeclick="typeclickload"></workbench-form>
          <div class="card-header">
            <span class="cartitle">待输液</span>
            <div>
              <el-button style=" padding: 3px 0" type="text" @click="btnoverlookidlist('dsy')">忽略全部</el-button>
            <el-badge :value="patientdsylistsums<10?patientdsylistsums:'10+'" class="badgeitem"  @click.native="onCreatePatient('dsy')"></el-badge>
            </div>
          </div>
        <div v-for="(item,index) in patientdsylistcounts.slice(0, 3)" :key="index"  class="text" >
          <a @click="skiptobeseen(item.patient.id,'dsy')"  class="cara1">{{ item.displaycontent }}</a><span class="cara2">
            <a  @click="skiptobeseen(item.patient.id,'dsy')" >就诊</a>
            <a  @click="btnoverlook(item.id,'dsy')"  style="margin-left:5px;">忽略</a></span></div>
      </el-card></el-col>
      <el-col  :lg="6" :xs="12" :sm="6" v-if="jyjctotal>0" ><el-card class="box-card" shadow="always">
        <workbench-form ref='workbenchForm' @typeclick="typeclickload"></workbench-form>
          <div class="card-header">
            <span class="cartitle">待检验检查</span>
            <div>
              <el-button style=" padding: 3px 0" type="text" @click="btnoverlookidlist('djyjc')">忽略全部</el-button>
            <el-badge :value="jyjctotal<10?jyjctotal:'10+'" class="badgeitem"  @click.native="onCreatePatient('djyjc')"></el-badge>
            </div>
          </div>
        <div v-for="(item,index) in jyjclistcounts.slice(0, 3)" :key="index"  class="text" >
          <a @click="skiptobeseen(item.patient.id,'djyjc')"  class="cara1">{{ item.patient.name }} / {{ item.patient.age }} / {{ item.name }}</a><span class="cara2">
            <a  @click="skiptobeseen(item.patient.id,'djyjc')" >就诊</a>
            <a  @click="btnoverlook(item.registration.id,'djyjc')"  style="margin-left:5px;">忽略</a></span></div>
      </el-card></el-col>
      </el-row>
    <el-row :gutter="10" class="rowcrad">
        <el-col :lg="6" :xs="12" :sm="6">
          <home-card :total="reviewTotal" :data="reviewList"
                     @clear-all='onClearAll'
                     @open-all='onOpenAll'
                     @click-content='onClickContent'
                     @click-one='onClickOne'
                     @click-two='onClickTwo'
          ></home-card>
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
  import {listInspectionCheckPage} from '@/api/cure/inspectionCheck'
  import { listRecipelInfoReviewPage } from '@/api/outpatient/recipelInfoReview'
  import {Loading} from 'element-ui'
  import HomeCard from "./components/homeCard";

  export default {
  extends: MainUI,
  components: {
    HomeCard,
    WorkbenchForm,
    History
  },
  data(){
return{
//  permission: {
//         view: false,
//         add: false,
//         edit: false,
//         remove: false,
//         export: false
//       },

      //今日待就诊患者列表
      TobeseenList: [],

      //今日待就诊患者总数
      Totalsums: "",
      //搜索患者信息
      SearchPatientInfo: "",
      //就诊搜索条件
      SearchPreModel: {
        columnName: "",
        limit: 10,
        offset: 0,
        order: "",

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
        overlook:"",
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
        limit: 10,
		columnName: '',      // 排序字段名
        order: ''            // 排序
      },
      jyjclistcounts:[],
      jyjctotal:0,

    //处方审查结果
    reviewTotal: 0,
    reviewList: [],
    reviewSearch: {
      params: [],
      offset: 0,
      limit: 3,
      columnName: '',       // 排序字段名
      order: ''             // 排序
    }
}
  },
  methods:{
typeclickload(obj){
  switch(obj){
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
      this.Totalsums=0;
      this.TobeseenList=[];
      let params = [
        {
          columnName: "company_id",
          queryType: "=",
          value: this.Company.id,
        },
        {
          columnName: "status",
          queryType: "=",
          value: "",
        },
        {
          columnName: "doctor_id",
          queryType: "=",
          value: this.UserId,
        },
        {
          columnName: "overlook",
          queryType: "=",
          value: "0",
        },
      ];

      this.SearchPreModel.params = params;
      this.SearchPreModel.params[1].value = "registrationStatus_0";//待就诊
      listSchedulesPage(this.SearchPreModel).then((responseData) => {
        if (responseData.code == 100) {
          this.Totalsums = responseData.data.total;

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
              // element.formatUpdateDate = moment(element.updateDate).format(
              //   "MM/DD HH:mm"
              // );
            });
          }
          this.PreparePatientList = responseData.data.rows;
        }
      });
    },
//获取待发药列表
GetDispensingTable() {
  this.dispensingsum=0;
  this.listdispensingcounts=[];
 this.PageRegistration.overlook="0",
 this.PageRegistration.columnName="return_date",
 this.PageRegistration.limit=10,
 this.PageRegistration.offset=0,
      this.PageRegistration.companyId=this.Company.id,
      listdispensingPages(this.PageRegistration).then((responseData) => {
        if (responseData.code == 100) {
          this.dispensingsum=responseData.data.total;
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
          })
          this.listdispensingcounts=responseData.data.rows;
        }
      });
    },

    //待收费
selectPatientchargeList(){
  this.Patientchargesum=0;
  this.PatientchargeList=[];
  this.patientQueryCondition.overlook="0";
this.patientQueryCondition.limit = 10;
      this.patientQueryCondition.companyId = this.Company.id;
      /*待收费*/
      this.isOnlyRead = false;
      this.patientQueryCondition.offset =0;
      this.patientQueryCondition.columnName = "reception_end_date";
      this.patientQueryCondition.order = "desc";
      this.patientQueryCondition.chargeStatus = 0;
      this.patientQueryCondition.updateDate = "2022-06-01 00:00:00";
      this.patientQueryCondition.status = "registrationStatus_1";
      this.patientQueryCondition.patientName=''
        this.patientQueryCondition.patientCode=''
        listdispensingPages(this.patientQueryCondition).then((responseData) => {
        if (responseData.code == 100) {
          this.Patientchargesum=responseData.data.total;
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
          })
          this.PatientchargeList=responseData.data.rows;
        }

       });
},

//待治疗
selectcurePatientList(){
  this.patientcuresum=0;
  this.patientcurecounts=[];
  this.patientcure.overlook="0",
   this.patientcure.limit = 10;
      this.patientcure.companyId = this.Company.id;
      /*待完成*/
      this.isOnlyRead = false;
      this.patientcure.offset =0;
      this.patientcure.columnName = "charge_date";
      this.patientcure.order = "desc";
      this.patientcure.chargeStatus = 1;
      this.patientcure.updateDate = "2022-06-01 00:00:00";
      this.patientcure.status = "registrationStatus_1";
      this.patientcure.cureType=0
      this.patientcure.patientName=''
        this.patientcure.patientCode=''

      listdispensingPages(this.patientcure).then((responseData) => {
        if (responseData.code == 100) {
          this.patientcuresum=responseData.data.total;
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
  this.patientdsylistsums=0;
  this.patientdsylistcounts=[];
  this.patientdsylist.overlook="0",
this.patientdsylist.limit = 10;
      this.patientdsylist.companyId = this.Company.id;
      /*待收费*/
      this.isOnlyRead = false;
      this.patientdsylist.offset =0;
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
  this.jyjclistcounts=[];
  this.jyjctotal=0;
  listInspectionCheckPage(this.searchjyjc).then(responseData => {
        if(responseData.code == 100) {
          console.info("jyjc:"+responseData);
          this.jyjclistcounts=responseData.data.rows;
          this.jyjctotal=responseData.data.total;
        } else {
        }
      }).catch(error => {
        this.outputError(error)
      })
},


//跳转路由
skiptobeseen(itemid,type){
//this.$router.push({path:'/medicalOutpatientRecord',query: {id:itemid}})
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

    onCreatePatient(types) {
      this.$refs.workbenchForm.$emit('openAddworkbenchDialog',types);
    },
    //获取Id进行忽略单条数据
    btnoverlook(values,types){
      let strRecipelInfoIds=values;
      updateoverlockids(strRecipelInfoIds).then((responseData) =>{
        this.$message.success(responseData.msg);
        this.typeclickload(types);
      });
    },
//忽略全部
btnoverlookidlist(values){
let idlist=[];
switch(values){
  case "djz":
    this.Totalsums=0;
      this.TobeseenList=[];
      this.SearchPreModel.companyId=this.Company.id,
        this.SearchPreModel.doctorid=this.UserId,
        this.SearchPreModel.status="registrationStatus_0",
        this.SearchPreModel.overlook="0",
updateoverlockidlist(this.SearchPreModel).then((responseData)=>{
  this.typeclickload(values);
  this.$message.success(responseData.msg);

})
break;
case "dsf":
this.Patientchargesum=0;
  this.PatientchargeList=[];
  this.patientQueryCondition.overlook="0";
this.patientQueryCondition.limit = 10;
      this.patientQueryCondition.companyId = this.Company.id;
      /*待收费*/
      this.isOnlyRead = false;
      this.patientQueryCondition.offset =0;
      this.patientQueryCondition.columnName = "reception_end_date";
      this.patientQueryCondition.order = "desc";
      this.patientQueryCondition.chargeStatus = 0;
      this.patientQueryCondition.updateDate = "2022-06-01 00:00:00";
      this.patientQueryCondition.status = "registrationStatus_1";
      this.patientQueryCondition.patientName=''
        this.patientQueryCondition.patientCode=''
updateoverlockidlist(this.patientQueryCondition).then((responseData)=>{
  this.typeclickload(values);
  this.$message.success(responseData.msg);

})
  break;
  case "dfy":
    this.dispensingsum=0;
  this.listdispensingcounts=[];
 this.PageRegistration.overlook="0",
 this.PageRegistration.columnName="return_date",
 this.PageRegistration.limit=10,
 this.PageRegistration.offset=0,
      this.PageRegistration.companyId=this.Company.id,
updateoverlockidlist(this.PageRegistration).then((responseData)=>{
  this.typeclickload(values);
  this.$message.success(responseData.msg);

})
  break;
  case "dzl":
  this.patientcuresum=0;
  this.patientcurecounts=[];
  this.patientcure.overlook="0",
   this.patientcure.limit = 10;
      this.patientcure.companyId = this.Company.id;
      /*待完成*/
      this.isOnlyRead = false;
      this.patientcure.offset =0;
      this.patientcure.columnName = "charge_date";
      this.patientcure.order = "desc";
      this.patientcure.chargeStatus = 1;
      this.patientcure.updateDate = "2022-06-01 00:00:00";
      this.patientcure.status = "registrationStatus_1";
      this.patientcure.cureType=0
      this.patientcure.patientName=''
        this.patientcure.patientCode=''
updateoverlockidlist(this.patientcure).then((responseData)=>{
  this.typeclickload(values);
  this.$message.success(responseData.msg);

})
  break;
  case "dsy":
  this.patientdsylistsums=0;
  this.patientdsylistcounts=[];
  this.patientdsylist.overlook="0",
this.patientdsylist.limit = 10;
      this.patientdsylist.companyId = this.Company.id;
      /*待收费*/
      this.isOnlyRead = false;
      this.patientdsylist.offset =0;
      this.patientList = [];
      this.patientdsylist.columnName = "dispensing_date";
      this.patientdsylist.order = "desc";
      this.patientdsylist.updateDate = "2022-06-01 00:00:00";
      this.patientdsylist.status = "registrationStatus_1";
      this.patientdsylist.infuseType=0;
      this.patientdsylist.patientName='';
      this.patientdsylist.patientCode='';
updateoverlockidlist(this.patientdsylist).then((responseData)=>{
  this.typeclickload(values);
  this.$message.success(responseData.msg);

})
  break;
  case "djyjc":
    this.searchjyjc.companyId=this.Company.id,
    this.searchjyjc.status="0",
    this.searchjyjc.overlook="0",
updateoverlockidlist(this.searchjyjc).then((responseData)=>{
  this.typeclickload(values);
  this.$message.success(responseData.msg);
})
  break;
};


// updateoverlockidlist

},

loadlist(){
  let loadingInstance =  Loading.service({fullscreen: true });
  // this.setLoad()
//待检验检查
 this.jyjclistcounts=[];
  this.jyjctotal=0;
  listInspectionCheckPage(this.searchjyjc).then(responseData => {
        if(responseData.code == 100) {
          console.info("jyjc:"+responseData);
          this.jyjclistcounts=responseData.data.rows;
          this.jyjctotal=responseData.data.total;
        } else {
        }
      }).catch(error => {
        this.outputError(error)
      });

//待输液
this.patientdsylistsums=0;
  this.patientdsylistcounts=[];
  this.patientdsylist.overlook="0",
this.patientdsylist.limit = 10;
      this.patientdsylist.companyId = this.Company.id;
      /*待收费*/
      this.isOnlyRead = false;
      this.patientdsylist.offset =0;
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
          })
          this.patientdsylistcounts=responseData.data.rows;
        }

       })
        .catch((error) => {
          console.log(error);
          this.$message.error(error);
        });
//待治疗
this.patientcuresum=0;
  this.patientcurecounts=[];
  this.patientcure.overlook="0",
   this.patientcure.limit = 10;
      this.patientcure.companyId = this.Company.id;
      /*待完成*/
      this.isOnlyRead = false;
      this.patientcure.offset =0;
      this.patientcure.columnName = "charge_date";
      this.patientcure.order = "desc";
      this.patientcure.chargeStatus = 1;
      this.patientcure.updateDate = "2022-06-01 00:00:00";
      this.patientcure.status = "registrationStatus_1";
      this.patientcure.cureType=0
      this.patientcure.patientName=''
        this.patientcure.patientCode=''

      listdispensingPages(this.patientcure).then((responseData) => {
        if (responseData.code == 100) {
          this.patientcuresum=responseData.data.total;
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
          })
          this.patientcurecounts=responseData.data.rows;
        }

       })
        .catch((error) => {
          console.log(error);
          this.$message.error(error);
        });

//待收费
this.Patientchargesum=0;
  this.PatientchargeList=[];
  this.patientQueryCondition.overlook="0";
this.patientQueryCondition.limit = 10;
      this.patientQueryCondition.companyId = this.Company.id;
      /*待收费*/
      this.isOnlyRead = false;
      this.patientQueryCondition.offset =0;
      this.patientQueryCondition.columnName = "reception_end_date";
      this.patientQueryCondition.order = "desc";
      this.patientQueryCondition.chargeStatus = 0;
      this.patientQueryCondition.updateDate = "2022-06-01 00:00:00";
      this.patientQueryCondition.status = "registrationStatus_1";
      this.patientQueryCondition.patientName=''
        this.patientQueryCondition.patientCode=''
        listdispensingPages(this.patientQueryCondition).then((responseData) => {
        if (responseData.code == 100) {
          this.Patientchargesum=responseData.data.total;
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
          })
          this.PatientchargeList=responseData.data.rows;
        }

       });


//待发药
this.dispensingsum=0;
  this.listdispensingcounts=[];
 this.PageRegistration.overlook="0",
 this.PageRegistration.columnName="return_date",
 this.PageRegistration.limit=10,
 this.PageRegistration.offset=0,
      this.PageRegistration.companyId=this.Company.id,
      listdispensingPages(this.PageRegistration).then((responseData) => {
        if (responseData.code == 100) {
          this.dispensingsum=responseData.data.total;
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
          })
          this.listdispensingcounts=responseData.data.rows;
        }
      });


//待就诊
this.Totalsums=0;
      this.TobeseenList=[];
      let params = [
        {
          columnName: "company_id",
          queryType: "=",
          value: this.Company.id,
        },
        {
          columnName: "status",
          queryType: "=",
          value: "",
        },
        {
          columnName: "doctor_id",
          queryType: "=",
          value: this.UserId,
        },
        {
          columnName: "overlook",
          queryType: "=",
          value: "0",
        },
      ];

      this.SearchPreModel.params = params;
      this.SearchPreModel.params[1].value = "registrationStatus_0";//待就诊
      listSchedulesPage(this.SearchPreModel).then((responseData) => {
        if (responseData.code == 100) {
          this.Totalsums = responseData.data.total;

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
              // element.formatUpdateDate = moment(element.updateDate).format(
              //   "MM/DD HH:mm"
              // );
            });
          }
          this.PreparePatientList = responseData.data.rows;
        }
      });
      setTimeout(() => {
            loadingInstance.close();
          }, 2000);

// this.$nextTick(() => { // 以服务的方式调用的 Loading 需要异步关闭 loadingInstance.close();
//  });
},
  initReviewList(){
    this.reviewSearch.params.push({
      columnName: 'recipelInfo.company_id',
      queryType: '=',
      value: this.currentUser.company.id
    })
    this.reviewSearch.params.push({
      columnName: 'registration.doctor_id',
      queryType: '=',
      value: this.currentUser.id
    })
    /*this.reviewSearch.params.push({
      columnName: 'review_status',
      queryType: '=',
      value: 1
    })*/
    this.reviewSearch.params.push({
      columnName: 'review_result',
      queryType: '=',
      value: 0
    })
    this.reviewSearch.params.push({
      columnName: 'recipelInfo.not_show',
      queryType: '=',
      value: 1
    })
    listRecipelInfoReviewPage(this.reviewSearch).then(responseData => {
      if(responseData.code == 100) {
        this.reviewTotal = responseData.data.total
        let list = new Array();
        if (responseData.data.rows){
          console.log(responseData.data,"--------------------------")
          responseData.data.rows.forEach(row =>{
            list.push({recipelInfoId:row.recipelInfo.id,content: row.recipelInfo.registration.patientId.name + "/" + row.recipelInfo.recipelType.name + "/" + row.reviewContent})
          })
        }

        this.reviewList = list
      } else {
        this.showMessage(responseData)
      }
      this.resetLoad()
    }).catch(error => {
      this.outputError(error)
    })
  },
    onClearAll(data){
      console.log(data,"onClearAll")
      data.forEach(item => {
        this.updateNotShowById(item.recipelInfoId,"all")
      })
      this.showMessage({type: 'success', msg: '操作成功'})
    },
    onOpenAll(){
      console.log("onOpenAll")
    },
    onClickContent(item){
      console.log(item,"onClickContent")
      this.$router.push({ path: '/recipelInfoReviewResult', params: {}})
    },
    onClickOne(item){
      console.log(item,"onClickOne")
      this.$router.push({ path: '/recipelInfoReviewResult', params: {}})
    },
    onClickTwo(item){
      console.log(item,"onClickTwo")
      this.updateNotShowById(item.recipelInfoId,"single")
    },
    updateNotShowById(recipelInfoId,type){
      updateNotShowById(recipelInfoId).then(responseData =>{
        if (responseData.code = 100) {
          this.initReviewList()
          if ("all" != type) {
            this.showMessage({type: 'success', msg: '操作成功'});
          }
        }
      })
    }
},

  computed: {
    Company() {
      let company = JSON.parse(sessionStorage.getItem("currentUser")).company;
      return {
        id: company.id,
        label: company.label,
        name: company.name,
      };
    },
    UserId() {
      return JSON.parse(sessionStorage.getItem("currentUser")).id;
    },
  },
  mounted(){
    //   this.GetPreparePatientList();
    //   this.selectPatientchargeList();
    //   this.GetDispensingTable();
    //   this.selectcurePatientList();
    //   this.selectpatientdsylist();
    // this.selectpatientdjyjclist();
    this.loadlist();
    this.initReviewList();

  },
}
</script>

<style lang="scss">
.rowcrad .el-card{
  height: 160px;
}
.el-col{
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
  .el-card__header{
    padding: 8px;
  }

  .el-card__body{
    height: auto;
    padding: 0 10px 0 10px;
  }

  .text {
    padding: 10px 0 0 0;
    font-size: 14px;
    width: 100%;
  text-align: left;
  clear:both;
  }

  .cartitle{
    font-weight:bold;
    color: #018cb7;
  }

  .cara1{
  display:inline-block;

  text-align: left;
  width: calc(100% - 90px);
  overflow:hidden;
  word-break:keep-all;
  white-space:nowrap;
  text-overflow:ellipsis;
  text-decoration:none;
  color:#333;
  clear:both;
  }

  .cara1:hover{
    color:#018cb7;
    cursor: pointer;
  }

  .cara2{
  display:inline;
  float: right;

  clear:both;
  }
  .cara2 a{
    text-decoration:none;
  color:#333;
  }
  .cara2 a:hover{
    color:#018cb7;
    cursor: pointer;
  }

  .badgeitem{
    height: auto;
    line-height:0%;
  }
</style>





