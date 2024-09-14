<template>
<el-car>
<el-row>
    <el-col :span="24">
      <el-form :inline="true" :model="strc" class="demo-form-inline">
      <el-form-item label="患者姓名" style="margin-right: 3px;">
    <el-input v-model="strc.hzxm" placeholder="请输入患者姓名"></el-input>
  </el-form-item>
   <el-form-item label="查询时间"  style="margin-right: 3px;">
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
  <el-form-item label="执行状态"  style="margin-right: 3px;">
  <el-select v-model="strc.zxzt" placeholder="执行状态">
      <el-option label="全部" value="qb"></el-option>
      <el-option label="未皮试" value="0"></el-option>
      <el-option label="已皮试" value="1"></el-option>
    </el-select>
  </el-form-item>
  <el-form-item label="开单医生" style="margin-right: 3px;">
    <el-input v-model="strc.kdys" placeholder="请输入开单医生"></el-input>
  </el-form-item>
    <el-form-item style="margin-right: 3px;">
    <el-button type="primary" @click="onSubmit">查询</el-button>
    <el-button type="info" icon="el-icon-refresh-left" @click='resetCondition' :plain='true'>重置</el-button>
    <!-- <el-button @click="show3 = !show3">{{show3==true?"收起":"展开"}}</el-button> -->
  </el-form-item>
      </el-form>
    </el-col>
    <el-col :span="24">
  <el-table
    :data="tableData"
    border
    height="calc(100vh - 250px)">
    <el-table-column
             label="序号"
              type="index"
              :index="blmbindexMethod"
              align="center">
            </el-table-column>
    <el-table-column
      prop="pszt"
      label="皮试状态">
    </el-table-column>
    <el-table-column
      prop="hzxm"
      label="患者">
    </el-table-column>
    <el-table-column
      prop="gmqk"
      label="过敏情况">
    </el-table-column>
    <el-table-column
      prop="kdsj"
      label="开单时间"
      width="150">
    </el-table-column>
    <el-table-column
      prop="kdys"
      label="开单医生"
      width="130">
    </el-table-column>
    <el-table-column
      prop="ypmc"
      label="药品">
    </el-table-column>
    <el-table-column prop="dosis" label="规格" width="150">
      <template slot-scope="scope">
        <template>
          {{scope.row.dosis}}{{scope.row.dosisunit}}*{{scope.row.preparation}}{{scope.row.preparationunit}}/{{scope.row.pack}}
        </template>
      </template>
     </el-table-column>
     <el-table-column
      prop="yf"
      label="用法">
    </el-table-column>
    <el-table-column
      prop="zxsj"
      label="执行时间"
      width="150">
    </el-table-column>
    <el-table-column
      prop="zxr"
      label="执行人"
      width="130">
    </el-table-column>
    <el-table-column  label="皮试结果" :width="140" fixed="right">
       <template slot-scope="scope">
      <el-select v-model="scope.row.stresult" filterable allow-create @change="psjgchange(scope.row)">
      <el-option label="阴性" value="0"></el-option>
      <el-option label="阳性" value="1"></el-option>
    </el-select>
        </template>
</el-table-column>
<el-table-column  label="皮试时间" :width="140" fixed="right" >
       <template slot-scope="scope">
      <el-select v-model="scope.row.sttime" filterable allow-create @change="pssjchange(scope.row)">
      <el-option label="5分钟" value="5"></el-option>
      <el-option label="10分钟" value="10"></el-option>
      <el-option label="15分钟" value="15"></el-option>
      <el-option label="20分钟" value="20"></el-option>
      <el-option label="25分钟" value="25"></el-option>
      <el-option label="30分钟" value="30"></el-option>
      <el-option label="35分钟" value="35"></el-option>
      <el-option label="40分钟" value="40"></el-option>
    </el-select>
        </template>
</el-table-column>
<el-table-column prop="intervalTime" label="倒计时" :width="140" fixed="right">
                <template slot-scope="scope">
                    <span>
                       {{ scope.row.intervalTime }}
                    </span>
                </template>
            </el-table-column>
    <el-table-column
      fixed="right"
      label="操作"
      width="100">
      <template slot-scope="scope">
        <el-button type="text" :disabled="scope.row.intervalTime!='' && scope.row.intervalTime!=null" @click.native="stclick(scope.$index,scope.row)">{{scope.row.stresult!='' && scope.row.stresult!=null ?"重新皮试":"开始皮试"}}</el-button>
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
</el-car>
</template>

<script>
import {stmanagementlist,updatestresult,updatesttime} from '@/api/cure/stmanagement'
  export default {
    
    data() {
      return {
          tableData:[],
        // tableData: [{
        //   date: '2016-05-02',
        //   name: '王小虎',
        //   province: '上海',
        //   city: '普陀区',
        //   address: '上海市普陀区金沙江路 1518 弄',
        //   zip: 200333,
        //   stresult:'',
        //   sttime:'',
        //   intervalTime:'',
        // }, {
        //   date: '2016-05-04',
        //   name: '王小虎',
        //   province: '上海',
        //   city: '普陀区',
        //   address: '上海市普陀区金沙江路 1517 弄',
        //   zip: 200333,
        //   stresult:'',
        //   sttime:'',
        //   intervalTime:'',
        // }, {
        //   date: '2016-05-01',
        //   name: '王小虎',
        //   province: '上海',
        //   city: '普陀区',
        //   address: '上海市普陀区金沙江路 1519 弄',
        //   zip: 200333,
        //   stresult:'',
        //   sttime:'',
        //   intervalTime:'',
        // }, {
        //   date: '2016-05-03',
        //   name: '王小虎',
        //   province: '上海',
        //   city: '普陀区',
        //   address: '上海市普陀区金沙江路 1516 弄',
        //   zip: 200333,
        //   stresult:'',
        //   sttime:'',
        //   intervalTime:'',
        // }],
        currentPage: 1,
        patientTotal: 0,
        setIntervaltime:[],
        strc:{
            limit: 20,
            offset: 0,
            companyId: currentUser.company.id,
            hzxm:'',
            kdys:'',
            zxzt:'',
            kssj:'',
            jssj:'',
            detailid:'',
            zxr:'',
            stresult:'',
            sttime:'',
        },
        stdisabled:false,
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
      //时间选择
      TimeInterval:this.getInitializeDate(),
      }
    },
    methods: {
        onSizeChange(val) {
      this.currentPage = 1
      this.strc.limit = val;
      this.strc.offset = (this.currentPage - 1) * val
      this.Getblmbtable();
    },
    onCurrentChange(val) {
      this.strc.offset = (val - 1) * this.strc.limit
      this.currentPage = val
      this.Getblmbtable();
    },
    blmbindexMethod(index){
       return (this.currentPage-1)*this.strc.limit+index +1;
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
      /** 倒计时*/
    countDown(row,index) {
     if( !this.setIntervaltime.length>0){
        this.setIntervaltime = new Array(this.tableData.length);
     };
      clearInterval(this.setIntervaltime[index]);
    // acceptanceOverTime 是我后台返回的截止时间
      if(row.sttime >0 ){
          let set = Date.parse(new Date()) //创建时间 获得时间戳
          const end = set + 1000 * 60 * row.sttime  //结束 在当前时间戳的基础上添加30分钟
        this.setIntervaltime[index] =setInterval(() => {
       //   row.intervalTime =  this.formatDuring(end - new Date());
     //  this.tableData[index].intervalTime = 1
       this.$set(this.tableData[index],'intervalTime',this.formatDuring(end - new Date()));
          console.log(this.tableData[index].intervalTime,'时间');
          if(row.intervalTime==""){clearInterval(this.setIntervaltime[index]);}
        }, 1000); //这边采取的是1秒调用一次
        //这里采用简单写法，直接在查询的时候调用一次，不用等定时器调用
        //row.intervalTime = this.formatDuring(new Date(row.sttime) - new Date());
      }
    },
    formatDuring (mss) {
      if(mss > 0){
        let hours = parseInt((mss % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60)) // 得到小时
        let minutes = parseInt((mss % (1000 * 60 * 60)) / (1000 * 60)) // 得到分钟数
        //获取分钟
        let str1 = minutes < 10 ? ('0' + minutes) : minutes
        
        /*得到秒数*/
        // let seconds = (mss % (1000 * 60)) / 1000
        let seconds=Math.floor((mss / 1000) % 60);
        let str2 = seconds < 10 ? ('0' + seconds) : seconds
        return str1 + ' : ' + str2 ;
      }else{
        return '';
      }
    },
    //开始皮试
    stclick(index,row){
      console.log(row,'看');
        this.countDown(row,index);
    },
    //重置
    resetCondition(){
         this.strc={
            limit: 20,
            offset: 0,
            companyId: currentUser.company.id,
            hzxm:'',
            kdys:'',
            zxzt:'',
            kssj:'',
            jssj:'',
            detailid:'',
            zxr:'',
            stresult:'',
            sttime:'',
        };
        this.TimeInterval=this.getInitializeDate();
        this.Getstmanagementlist();
    },
    //获取数据
    Getstmanagementlist(){
        this.tableData=[];
        if(this.TimeInterval){
        this.strc.kssj=this.TimeInterval[0];
        this.strc.jssj=this.TimeInterval[1];
        this.strc.zxzt=this.strc.zxzt=="qb"?"":this.strc.zxzt;
        this.strc.kdys=this.strc.kdys;
        this.strc.hzxm=this.strc.hzxm;
        stmanagementlist(this.strc).then((responseData)=>{
            if (responseData.code == 100){
                this.patientTotal=responseData.data.total;
                if(responseData.data.total>0){
                    responseData.data.rows.forEach((item)=>{
                        if(item.pszt=='0'){item.pszt="未皮试"}else if(item.pszt=='1'){item.pszt="已皮试"}else{item.pszt=""}
                        this.tableData.push({
                            detailid:item.detailid,
                            pszt:item.pszt,
                            hzxm:item.hzxm,
                            gmqk:item.gmqk,
                            kdsj:item.kdsj,
                            kdys:item.kdys.substring(0,item.kdys.lastIndexOf("(")),
                            ypmc:item.ypmc,
                            dosis:item.dosis,
                            dosisunit:item.dosisunit,
                            preparation:item.preparation,
                            preparationunit:item.preparationunit,
                            pack:item.pack,
                            yf:item.yf,
                            zxsj:item.zxsj,
                            zxr:item.zxr!='' && item.zxr!=null?item.zxr.substring(0,item.zxr.lastIndexOf("(")):item.zxr,
                            stresult:item.stresult,
                            sttime:item.sttime,
                        })
                    })
                    
                }
            }
        })
  }
    },
    onSubmit() {
        this.Getstmanagementlist();
        
      },
      psjgchange(value){
          this.strc.zxr=currentUser.name+"("+currentUser.loginname+")";
          this.strc.detailid=value.detailid;
          this.strc.stresult=value.stresult;
          updatestresult(this.strc).then((responseData)=>{
              if (responseData.code == 100){
                  this.Getstmanagementlist();
                  this.$message.success(responseData.msg);
              }
          })
      },
      pssjchange(value){
          this.strc.sttime=value.sttime;
          this.strc.detailid=value.detailid;
          updatesttime(this.strc).then((responseData)=>{
              if(responseData.code==100){
                  this.Getstmanagementlist();
                  this.$message.success(responseData.msg);
              }
          })
      },
    },
    mounted(){
      this.Getstmanagementlist();
    }
  }
</script>

<style lang="scss">
.el-picker-panel__footer .el-picker-panel__link-btn.el-button--text {
  display: none;
}
</style>