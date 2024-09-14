<template>

<el-card class="page-container">
<el-row>
  <el-col :span="24">
    <el-form :inline="true" :model="formInline" class="demo-form-inline">
    <el-form-item label="选择查询时间">
  <div class="block">
    <el-date-picker
      v-model="TimeInterval"
      type="datetimerange"
      value-format="yyyy-MM-dd HH:mm:ss"
      :picker-options="pickerOptions"
      range-separator="至"
      start-placeholder="开始日期"
      end-placeholder="结束日期"
      align="right">
    </el-date-picker>
  </div>
    </el-form-item>
      
  <el-form-item label="查询患者">
    <el-input v-model="formInline.values" placeholder="请输入姓名/手机号/身份证"></el-input>
  </el-form-item>
  <el-form-item label="就诊状态">
    <el-select v-model="formInline.status" placeholder="就诊状态">
      <el-option label="全部" value="qb"></el-option>
      <el-option label="待就诊" value="registrationStatus_0"></el-option>
      <el-option label="已就诊" value="registrationStatus_1"></el-option>
      <el-option label="已取消" value="registrationStatus_2"></el-option>
      <el-option label="待签到" value="registrationStatus_3"></el-option>
      <el-option label="已失效" value="registrationStatus_4"></el-option>
    </el-select>
  </el-form-item>
  <el-form-item>
    <el-button type="primary" @click="onSubmit">查询</el-button>
    <el-button
      type="info"
      icon="el-icon-refresh-left"
      @click="reset"
      :plain="true"
    >重置</el-button>
  </el-form-item>
</el-form>
  </el-col>
  <el-col :span="24">
    <el-table
    :data="tableData"
    height="calc(100vh - 280px)"
    style="width: 100%"
    :row-class-name="tableRowClassName">
    <el-table-column
             label="序号"
              type="index"
              :index="indexMethod"
              align="center">
            </el-table-column>
    <el-table-column
      prop="jzzt"
      label="就诊状态"
      >
    </el-table-column>
    <!-- <el-table-column
      prop="xm"
      label="患者"
      >
    </el-table-column> -->
<el-table-column
      label="患者">
      <template slot-scope="scope">
        <!-- <el-popover trigger="hover" placement="top" >
          <span><a>{{ scope.row.xm }}</a><a style="margin-left:10px;">{{ scope.row.xb }}</a><a style="margin-left:10px;">{{ scope.row.sr }}</a><a style="margin-left:10px;">{{ scope.row.phone }}</a></span>
          <div slot="reference" style="width:auto;">
            <span>{{ scope.row.xm }}</span>
          </div>
        </el-popover> -->
        <el-popover
    placement="top-start"
    trigger="hover">
    <v-slot:content>
      <a>{{ scope.row.xm }}</a><a style="margin-left:10px;">{{ scope.row.xb }}</a><a style="margin-left:10px;">{{ scope.row.sr }}</a><a style="margin-left:10px;">{{ scope.row.phone }}</a>
      </v-slot:content>
    <a slot="reference">{{ scope.row.xm }}</a>
  </el-popover>
 </template>
    </el-table-column>


    <el-table-column
      prop="ks"
      label="科室"
      >
    </el-table-column>
    <el-table-column
      prop="djsj"
      label="登记时间"
      >
    </el-table-column>
    <el-table-column
      prop="jzlx"
      label="接诊类型"
      >
    </el-table-column>
    <el-table-column
      prop="ys"
      label="接诊医生"
      >
    </el-table-column>
    <el-table-column
      prop="cfs"
      label="处方数量"
      >
    </el-table-column>
  </el-table>
  </el-col>
</el-row>
<el-row>
        <el-col :span='24'>
          <el-pagination
            background     
            @size-change='onSizeChange'
            @current-change='onCurrentChange'
            :current-page.sync='currentPage'
            :page-sizes='[ 20, 50, 100, patientTotal]'
            :page-size='20'
            layout='total, sizes, prev, pager, next, jumper'
            :total='patientTotal'>
          </el-pagination>
        </el-col>
      </el-row>
</el-card>
</template>



<script>
import MainUI from '@/views/components/mainUI'
import History from '@/views/components/history'
import {
  selectvisiprogresslist,
}  from "@/api/workbench/Schedules";
import moment from "moment"; 
  export default {
    extends: MainUI,
  components: { 
    History
  },
    methods: {
      tableRowClassName({row, rowIndex}) {
        if (rowIndex === 1) {
          return 'warning-row';
        } else if (rowIndex === 3) {
          return 'success-row';
        }
        return '';
      }
    },
    data() {
      return {
        formInline: {
          values: '',
          status: ''
        },
        tableData: [{
          jzzt:"",
          xm:"",
          ks:"",
          djsj:"",
          jzlx:"",
          ys:"",
          cfs:"",
          xb:"",
          sr:"",
          phone:"",
        }],
        visitprogresspara: {
        limit: 20,
        offset: 0,
        companyId: currentUser.company.id,
        status: "",
        values:"",
        kssj:"",
        jssj:"",
      },
      currentPage: 1,
      patientTotal: 0,
      patientList: [],

      //时间选择
      TimeInterval:'',

pickerOptions: {
          shortcuts: [{
            text: '最近一周',
            onClick(picker) {
              const end = new Date();
              const start = new Date();
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 7);
              picker.$emit('pick', [start, end]);
            }
          }, {
            text: '最近一个月',
            onClick(picker) {
              const end = new Date();
              const start = new Date();
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 30);
              picker.$emit('pick', [start, end]);
            }
          }, {
            text: '最近三个月',
            onClick(picker) {
              const end = new Date();
              const start = new Date();
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 90);
              picker.$emit('pick', [start, end]);
            }
          }]
        },

      }
    },
    methods: {

      //重置
      reset(){
        this. formInline = {
          values: '',
          status: ''
        };
        this.TimeInterval='';
        this. visitprogresspara = {
        limit: 20,
        offset: 0,
        companyId: currentUser.company.id,
        status: "",
        values:"",
        kssj:"",
        jssj:"",
        };
        this.Getvisiprogresslist();
      },

      onSizeChange(val) {
      this.currentPage = 1
      this.visitprogresspara.limit = val;
      this.visitprogresspara.offset = (this.currentPage - 1) * val
      this.Getvisiprogresslist();
    },
    onCurrentChange(val) {
      this.visitprogresspara.offset = (val - 1) * this.visitprogresspara.limit
      this.currentPage = val
      this.Getvisiprogresslist();
    },
    indexMethod(index){
       return (this.currentPage-1)*this.visitprogresspara.limit+index +1;
    },

 Getvisiprogresslist(){
  this.tableData=[];
  if(this.TimeInterval){
    this.visitprogresspara.kssj=this.TimeInterval[0];
  this.visitprogresspara.jssj=this.TimeInterval[1]
  }
  else{
    this.visitprogresspara.kssj="";
    this.visitprogresspara.jssj=""
  }
this.visitprogresspara.status=this.formInline.status=="qb"?"":this.formInline.status;
this.visitprogresspara.values=this.formInline.values;
selectvisiprogresslist(this.visitprogresspara).then((responseData)=>{
  if (responseData.code == 100){
    this.patientTotal=responseData.data.total;
    console.log(responseData,'看看');
    if( responseData.data.rows!=null){
    responseData.data.rows.forEach((item)=>{
      switch(item.jzzt.trim()){
        case "registrationStatus_0":
          item.jzzt="待就诊"
          break;
        case "registrationStatus_1":
          item.jzzt="已就诊"
          break;
        case "registrationStatus_2":
          item.jzzt="已取消"
          break;
        case "registrationStatus_3":
          item.jzzt="待签到"
          break;
        case "registrationStatus_4":
          item.jzzt="已失效"
          break;
        case "registrationStatus_5":
          item.jzzt="待分诊"
          break;
      };
      switch(item.jzlx){
        case "treatType_0":
          item.jzlx="初诊"
          break;
          case "treatType_1":
          item.jzlx="复诊"
          break;
      };
          let birthday = item.sr;
          if (birthday) {
                const duration = moment.duration(moment().diff(birthday));
                item.sr = duration.years();
              } else {
                item.sr = "--";
              }
      this.tableData.push({
          jzzt:item.jzzt,
          xm:item.xm,
          ks:item.ks,
          djsj:item.djsj,
          jzlx:item.jzlx,
          ys:item.ys,
          cfs:item.cfs,
          xb:item.xb,
          sr:item.sr,
          phone:item.phone,
      })
     console.log(this.tableData,'乞丐i');
    })
    }
  }
})

},

      onSubmit() {
        this.Getvisiprogresslist();
      },

    },
  //  created(){
  //   this.Getvisiprogresslist();
  // },
    mounted(){
      this.Getvisiprogresslist();
    }
  }
</script>

<style>
 .el-table .warning-row {
    background: oldlace;
  }

  .el-table .success-row {
    background: #f0f9eb;
  }
  .el-popover{
    overflow-y: visible;
  }
</style>






