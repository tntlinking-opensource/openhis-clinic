<template>
<el-card class="page-container" style="padding: 0px;">
  <el-row>
    <el-col :span="24" style="height:40px;">
      <addtask-form ref='addtaskForm' @typeclick="typeclickload"></addtask-form>
      <el-button type="primary" style="float:right;margin-right:4.5%;" icon='el-icon-plus'  @click.native="onCreatePatient('')">新增</el-button>
    </el-col>
    <el-col :span="24">
     <el-form :inline="true" :model="formInline" class="demo-form-inline">
    <el-form-item label="截止时间"  style="margin-right: 3px;">
  <div class="block" style="width:50%">
    <el-date-picker
      v-model="TimeInterval"
      type="datetimerange"
      value-format="yyyy-MM-dd HH:mm:ss"
      :picker-options="pickerOptions"
      :default-time="['00:00:00','23:59:59']"
      range-separator="至"
      start-placeholder="开始日期"
      end-placeholder="结束日期"
      align="right"
      :clearable="false">
    </el-date-picker>
  </div>
    </el-form-item>
    <!-- <el-form-item label="诊所"  style="margin-right: 3px;">
    <el-select v-model="formInline.jgid" placeholder="诊所">
      <el-option label="全部" value="qb"></el-option>
       <el-option v-for="item in jglist" :value="item.jgid" :key="item.jgid"  :label="item.jgmc">
        </el-option>
    </el-select>
  </el-form-item> -->
    <el-form-item label="任务类型"  style="margin-right: 3px;">
    <el-select v-model="formInline.tasktype" placeholder="任务类型">
      <el-option label="全部" value="qb"></el-option>
      <el-option label="常规任务" value="0"></el-option>
      <el-option label="宣传活动" value="1"></el-option>
    </el-select>
  </el-form-item>
  <el-form-item label="任务状态"  style="margin-right: 3px;">
    <el-select v-model="formInline.taskstatus" placeholder="任务状态">
      <el-option label="全部" value="qb"></el-option>
      <el-option label="待发布" value="0"></el-option>
      <el-option label="已发布" value="1"></el-option>
    </el-select>
  </el-form-item>
    <el-form-item label="任务名称" style="margin-right: 3px;">
    <el-input v-model="formInline.taskname" placeholder="请输入任务名称关键词"></el-input>
  </el-form-item>

  <el-form-item style="margin-right: 3px;">
    <el-button type="primary" @click="Getdatalist">查询</el-button>
    <el-button type="info" icon="el-icon-refresh-left" @click='resetCondition' :plain='true'>重置</el-button>
    <!-- <el-button @click="show3 = !show3">{{show3==true?"收起":"展开"}}</el-button> -->
  </el-form-item>
     </el-form>
   </el-col>
    <el-col :span="24">
      <taskdetails-form ref='taskdetailsForm' @typeclick="typeclickload"></taskdetails-form>
  <el-table
    :data="tableData"
    border
    style="width: 100%">
    <el-table-column
             label="序号"
              type="index"
              :index="taskindexMethod"
              align="center">
            </el-table-column>
    <!-- <el-table-column
      prop="date"
      label="日期"
      width="150">
    </el-table-column> -->
    <el-table-column
      prop="id"
      v-if="show"
      label="id">
    </el-table-column>
    <el-table-column
      prop="tasktype"
      label="任务类型"
      width="width">
    </el-table-column>
    <!-- <el-table-column
      prop="taskstatus"
      label="任务状态"
      width="width">
    </el-table-column> -->
    <el-table-column
      prop="taskname"
      label="任务标题"
      width="width">
    </el-table-column>
    <!-- <el-table-column
      prop="city"
      label="市区"
      width="120">
    </el-table-column> -->
    <el-table-column
      prop="taskdescribe"
      label="任务内容"
      width="width">
    </el-table-column>
    <el-table-column
      prop="createdTime"
      label="发布时间"
      width="width">
    </el-table-column>
    <el-table-column
      prop="taskdeadline"
      label="任务截止时间"
      width="width">
    </el-table-column>
    <el-table-column
      prop="taskstatus"
      label="任务状态"
      width="width">
      <template slot-scope="scope">
        <span v-if="scope.row.taskstatus=='0'" style="color:orange">
            待发布
        </span>
         <span v-else style="color:lightgreen">
            已发布
        </span>
      </template>
    </el-table-column>
    <el-table-column
      prop="taskaccessory"
      v-if="show"
      label="taskaccessory">
    </el-table-column>
    <el-table-column
      fixed="right"
      label="操作"
      width="width">
      <template slot-scope="scope">
        <el-button v-if="scope.row.taskstatus=='1'" @click="onCreatedetails(scope.row,scope.$index)" type="text" size="small">查看</el-button>
        <el-button v-if="scope.row.taskstatus=='0'" @click="onUpdate(scope.row,scope.$index)" type="text" size="small">修改</el-button>
        <el-button v-if="scope.row.taskstatus=='0'" @click="onRelease(scope.row,scope.$index)" type="text" size="small" style="color:lightgreen">发布任务</el-button>
        <el-button v-if="scope.row.taskstatus=='0'" @click="onDelete(scope.row,scope.$index)" type="text" size="small" style="color:red;">删除任务</el-button>
        <!-- <el-button type="text" size="small">编辑</el-button> -->
      </template>
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
import AddtaskForm from './addtaskForm'
import TaskdetailsForm from './taskdetailsForm'
import MainUI from '@/views/components/mainUI'
import History from '@/views/components/history'
import {selecttasklist,storageTask,release,deleteTask} from "@/api/taskmanagement/taskmanagement"
import { getjglist} from "@/api/toll/tollInfo";
  export default {
    extends: MainUI,
  components: {
    AddtaskForm,
    TaskdetailsForm,
    History
  },
    data() {
      return {
        tableData: [],//表格数据
        jglist:[],
        show:false,
        gettaskrcvo: {
          limit: 20,
        offset: 0,
        jgid: '',
          kssj:'',
          jssj:'',
          tasktype:'',
          taskstatus:'',
          taskname:''
        },
        formInline: {
        jgid: '',
          kssj:'',
          jssj:'',
          tasktype:'',
          taskstatus:'',
          taskname:''
        },
        currentPage: 1,
        patientTotal: 0,
      patientList: [],
        TimeInterval:this.getInitializeDate(),
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
        YpjxcRc: {
        limit: 20,
        offset: 0,
        companyId: currentUser.company.id,
        orderby:"batch_no",
        jgid:null,
      },
      taskAll:[],
      zsids:'',

      }
    },
    methods: {

      //删除任务
      onDelete(row,index){
        this.setLoad();
        deleteTask(row).then((res)=>{
          if(res.code==100){
            this.$message.success("删除成功！")
            this.Getcliniclist()
            this.resetLoad();
          }else{
            this.$message.error("执行失败")
          }
        }).catch((error)=>{
          this.$message.error(error)
        })
      },

      //发布任务
      onRelease(row,index){
        row.taskstatus = "1"
        release(row).then((res)=>{
          if(res.code==100){
            this.$message.success("发布成功")
            this.Getcliniclist()
          }
        }).catch((error)=>{
          this.$message.error(error)
        })
      },

      //修改任务
      onUpdate(row,index){
        this.$refs.addtaskForm.$emit('openUpdateworkbenchDialog',this.taskAll[index]);
      },

         //设置默认日期
    getInitializeDate () {
     let date = new Date()//获取新的时间
     //获取当前年份,并且转为字符串
     let year = date.getFullYear().toString()
     //获取当前月份，因为月份是要从0开始，此处要加1，
    //使用三元表达式，判断是否小于10，如果是的话，就在字符串前面拼接'0'
     let month = date.getMonth() + 1 < 10 ? '0' + (date.getMonth() + 1).toString() : (date.getMonth() + 1).toString()
     //获取天，判断是否小于10，如果是的话，就在在字符串前面拼接'0'
     let day = date.getDate() < 10 ? '0' + date.getDate().toString() : date.getDate().toString()
     //字符串拼接，将开始时间和结束时间进行拼接
     let start = year + '-' + month + '-01'+' 00:00:00'    //当月第一天
     //let end=new Date(year, month, 0).getDate();
     let end = year + '-' + month + '-' + day +' 23:59:59'  //当天
     return [start,end] //将值设置给组件DatePicker 绑定的数据
   },
       onSizeChange(val) {
      this.currentPage = 1
      this.gettaskrcvo.limit = val;
      this.gettaskrcvo.offset = (this.currentPage - 1) * val
      this.Getcliniclist();
    },
    onCurrentChange(val) {
      this.gettaskrcvo.offset = (val - 1) * this.gettaskrcvo.limit
      this.currentPage = val
      this.Getcliniclist();
    },
    taskindexMethod(index){
       return (this.currentPage-1)*this.gettaskrcvo.limit+index +1;
    },
      handleClick(row) {
        console.log(row);
      },
      onCreatePatient(types) {
      this.$refs.addtaskForm.$emit('openAddworkbenchDialog',types);
    },
    onCreatedetails(values){

      this.$refs.taskdetailsForm.$emit('openAddworkbenchDialog',values);
    },
    typeclickload(){
      this. Getcliniclist();
    },
    resetCondition(){
      this.YpjxcRc={
        limit: 20,
        offset: 0,
        companyId: currentUser.company.id,
        orderby:"batch_no",
        jgid:null,
      };
      this.jglist=[];
        this.gettaskrcvo= {
          limit: 20,
        offset: 0,
        jgid: '',
          kssj:'',
          jssj:'',
          tasktype:'',
          taskstatus:'',
          taskname:''
        };
        this.formInline= {
        jgid: '',
          kssj:'',
          jssj:'',
          tasktype:'',
          taskstatus:'',
          taskname:''
        };
        this.Getcliniclist();
    },
     Getcliniclist(){
       this.setLoad();
          this.jglist=[];
          getjglist(this.YpjxcRc).then((responseData)=>{
              if (responseData.code == 100){
                  if(responseData.data.length>0){
                     this.zsids='';
                      responseData.data.forEach((item)=>{
                          if(item.jgid!=currentUser.company.id){
                          this.jglist.push({
                              jgid:item.jgid,
                              jgmc:item.jgmc,
                           })
                           this.zsids+=item.jgid +',';
                         }
                      })
                      this.zsids=this.zsids.substring(0, this.zsids.lastIndexOf(','));
                      //this.jglist = responseData.data.filter((item) => item.jgid !=currentUser.company.id)
                      this.Getdatalist();
                      this.resetLoad();
                  }
              }
          })
      },
      Getdatalist() {
        this.setLoad();
        this.tableData = [];
        console.log(this.TimeInterval,"TimeInterval")
        if (this.TimeInterval) {
          this.gettaskrcvo.kssj = this.TimeInterval[0];
          this.gettaskrcvo.jssj = this.TimeInterval[1]
        }else {
          this.gettaskrcvo.kssj = null;
          this.gettaskrcvo.jssj = null;
        }
        this.gettaskrcvo.tasktype = this.formInline.tasktype == "qb" ? "" : this.formInline.tasktype;
        this.gettaskrcvo.taskstatus = this.formInline.taskstatus == "qb" ? "" : this.formInline.taskstatus;
        this.gettaskrcvo.taskname = this.formInline.taskname;
        selecttasklist(this.gettaskrcvo).then((responseData) => {

          if (responseData.code == 100) {
            if (responseData.data.total > 0) {
              this.patientTotal = responseData.data.total;
              this.taskAll = responseData.data.rows
              responseData.data.rows.forEach((item) => {

                this.tableData.push({
                  id: item.id,
                  tasktype: item.tasktype == "0" ? "常规任务" : "宣传活动",
                  taskname: item.taskname,
                  taskdescribe: item.taskdescribe,
                  createdTime: item.createdTime,
                  taskdeadline: item.taskdeadline,
                  taskinitiatorname: item.taskinitiatorname,
                  taskaccessory: item.taskaccessory,
                  taskstatus: item.taskstatus,

                })
              })
            }
          }
          this.resetLoad();
        })

      },


    },
    mounted(){
        this.Getcliniclist();
    }
  }
</script>
