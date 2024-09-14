<template>
  <el-card class="page-container" style="padding: 0px">
    <el-row>
      <article-form ref="articleForm" @typeclick="typeclickload"></article-form>
      <el-col :span="24">
        <el-form :inline="true" :model="formInline" class="demo-form-inline">
          <el-form-item label="截止时间" style="margin-right: 3px">
            <div class="block" style="width: 50%">
              <el-date-picker
                v-model="TimeInterval"
                type="datetimerange"
                value-format="yyyy-MM-dd HH:mm:ss"
                :picker-options="pickerOptions"
                :default-time="['00:00:00', '23:59:59']"
                range-separator="至"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                align="right"
                :clearable="false"
              >
              </el-date-picker>
            </div>
          </el-form-item>
          <el-form-item label="诊所" style="margin-right: 3px">
            <el-select v-model="formInline.clinic" clearable placeholder="诊所">
              <el-option
                v-for="item in clinic"
                :value="item.id"
                :key="item.id"
                :label="item.name"
              >
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="审核状态" style="margin-right: 3px">
            <el-select v-model="formInline.auditstatus" clearable placeholder="审核状态">
              <el-option
                v-for="item in auditstatuslist"
                :value="item.auditstatus"
                :key="item.auditstatus"
                :label="item.auditstatusmc"
              >
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="文章标题" style="margin-right: 3px">
            <el-input
              v-model="formInline.title"
              placeholder="请输入关键词进行查询"
            ></el-input>
          </el-form-item>

          <el-form-item style="margin-right: 3px">
            <el-button type="primary" @click="onSearch">查询</el-button>
            <el-button
              type="info"
              icon="el-icon-refresh-left"
              @click="resetCondition"
              :plain="true"
              >重置</el-button
            >
            <!-- <el-button @click="show3 = !show3">{{show3==true?"收起":"展开"}}</el-button> -->

            <!-- <el-button
              type="primary"
              icon="el-icon-plus"
              @click.native="onCreatePatient('新增','')"
              >新增</el-button
            > -->
          </el-form-item>
        </el-form>
      </el-col>
    </el-row>
    <el-row>
      <el-col :span="24">
        <el-table :data="articlelist" border style="width: 100%">
          <el-table-column
            label="序号"
            type="index"
            :index="taskindexMethod"
            align="center"
          >
          </el-table-column>
        <el-table-column prop="id" label="标识ID" v-if="false" >
        </el-table-column>
          <el-table-column prop="title" label="文章标题" width="width" align="center">
          </el-table-column>
          <el-table-column prop="abstracts" label="文章摘要" width="width" align="center">
          </el-table-column>
          <el-table-column prop="aricledate" label="发布时间" width="width" align="center">
          </el-table-column>
          <el-table-column prop="username" label="发布人" width="width" align="center">
          </el-table-column>
          <el-table-column prop="auditstatus" label="审核状态" width="width" align="center"> 
            <template slot-scope="scope">
              <el-label
                v-if="scope.row.auditstatus == '0'"
                style="color: #a7761a"
                >{{ getauditstatus(scope.row.auditstatus) }}</el-label
              >
              <el-label
                v-if="scope.row.auditstatus == '1'"
                style="color: #3eec5b"
                >{{ getauditstatus(scope.row.auditstatus) }}</el-label
              >
              <el-label
                v-if="scope.row.auditstatus == '2'"
                style="color: #f5314b"
                >{{ getauditstatus(scope.row.auditstatus) }}</el-label
              >
            </template>
          </el-table-column>
          <el-table-column fixed="right" label="操作" width="120">
            <template slot-scope="scope">
              <el-button v-if="scope.row.auditstatus=='0'" @click="onCreatePatient('审核',scope.row.id)" type="text" size="small"
                >审 核</el-button
              >
               <el-button @click="onCreatePatient('查看',scope.row.id)" type="text" size="small"
                >查 看</el-button
              >
            </template>
          </el-table-column>
        </el-table>
      </el-col>
    </el-row>
    <el-row>
      <el-col :span="24">
        <el-pagination
          background
          @size-change="onSizeChange"
          @current-change="onCurrentChange"
          :current-page.sync="currentPage"
          :page-sizes="[20, 50, 100, patientTotal]"
          :page-size="20"
          layout="total, sizes, prev, pager, next, jumper"
          :total="patientTotal"
        >
        </el-pagination>
      </el-col>
    </el-row>
  </el-card>
</template>

<script>
import MainUI from "@/views/components/mainUI";
import History from "@/views/components/history";
import ArticleForm from "../article/articleForm";
import { listarticlepages } from "@/api/taskmanagement/article";
import {getCompanys} from "@/api/org/company"
export default {
  extends: MainUI,
  components: {
    History,
    ArticleForm,
  },
  data() {
    return {
      editoroption: {
        theme: "bubble",

        readOnly: true, //只读模式 true
      },
      articlelist: [],
      TimeInterval: this.getInitializeDate(),
      auditstatuslist: [
        { auditstatus: "0", auditstatusmc: "待审核" },
        { auditstatus: "1", auditstatusmc: "已通过" },
        { auditstatus: "2", auditstatusmc: "未通过" },
      ],
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
      formInline: {
        limit: 20,
        offset: 0,
        companyId: currentUser.company.id,
        title: "",
        lx: "",
        auditstatus: null,
        clinic:"",
      },
      currentPage: 1,
      patientTotal: 0,
      clinic:[],  //诊所
    };
  },
  mounted() {
    //this.editorop();
    this.getpageinit();
    this.initOptions();
  },
  methods: {
    
    //搜索重置
    resetCondition(){
        this.TimeInterval = this.getInitializeDate();
        this.formInline.title = "";
        this.formInline.auditstatus = null;
        this.formInline.clinic = ""
        this.getpageinit()
    },

    //初始化状态
    initOptions(){
     
      let id = currentUser.company.id
      getCompanys(id).then((res)=>{
        if(res.code==100){
          console.log(res.data);

          this.clinic = res.data.filter((item)=>
            item.id!=currentUser.company.id
          )
          console.log(this.clinic);
        }else{
          this.$message.error("执行失败！")
        }
      }).catch((error)=>{
        this.$message.error(error)
        })
    },

    //设置默认日期
    getInitializeDate() {
      let date = new Date(); //获取新的时间
      //获取当前年份,并且转为字符串
      let year = date.getFullYear().toString();
      //获取当前月份，因为月份是要从0开始，此处要加1，
      //使用三元表达式，判断是否小于10，如果是的话，就在字符串前面拼接'0'
      let month =
        date.getMonth() + 1 < 10
          ? "0" + (date.getMonth() + 1).toString()
          : (date.getMonth() + 1).toString();
      //获取天，判断是否小于10，如果是的话，就在在字符串前面拼接'0'
      let day =
        date.getDate() < 10
          ? "0" + date.getDate().toString()
          : date.getDate().toString();
      //字符串拼接，将开始时间和结束时间进行拼接
      let start = year + "-" + month + "-01" + " 00:00:00"; //当月第一天
      //let end=new Date(year, month, 0).getDate();
      let end = year + "-" + month + "-" + day + " 23:59:59"; //当天
      return [start, end]; //将值设置给组件DatePicker 绑定的数据
    },
    getauditstatus(status) {
      return status == "0" ? "待审核" : status == "1" ? "已通过" : "未通过";
    },
    typeclickload(){
        this.getpageinit();
    },
    onSearch() {
      this.search.offset = 0;
      this.currentPage = 1;
      this.getpageinit();
    },
    onSizeChange(val) {
      this.currentPage = 1;
      this.search.limit = val;
      this.search.offset = (this.currentPage - 1) * val;
      this.getpageinit();
    },
    onCurrentChange(val) {
      this.search.offset = (val - 1) * this.search.limit;
      this.currentPage = val;
      this.getpageinit();
    },
    taskindexMethod(index) {
      return (this.currentPage - 1) * this.search.limit + index + 1;
    },
    getcontenttext(evt) {
      // let reft=await  this.$refs.quilleditorref;
      // this.content=reft.handleGetHtml();
    },
    onCreatePatient(types,id) {
      console.log(currentUser);
      let viewlist={
          view:types,
          id:id
      };
      this.$refs.articleForm.$emit("openAddworkbenchDialog", viewlist);
    },

    getpageinit() {
      /* 查询参数 和数据权限 */
      this.search.params = [
        
      ];
      if (this.moreCodition) {
        this.search.params = this.search.params.concat(
          this.compositeCondition()
        );
      } else {
        // 查询参数: 标题名称
        this.search.params.push({
          columnName: "title",
          queryType: "like",
          value: this.formInline.title,
        });
        if (this.formInline.auditstatus) {
          // 查询参数: 审核状态
          this.search.params.push({
            columnName: "auditstatus",
            queryType: "=",
            value: this.formInline.auditstatus,
          });
        }

        if(this.formInline.clinic!=""){
          this.search.params.push(
            {
              columnName:"company_id",
              queryType:"=",
              value:this.formInline.clinic
            }
          )
        }

        if (this.TimeInterval) {
          // 查询参数: 发布开始时间
          this.search.params.push({
            columnName: "aricledate",
            queryType: ">=",
            value: this.TimeInterval[0],
          });
          // 查询参数: 发布结束时间
          this.search.params.push({
            columnName: "aricledate",
            queryType: "<=",
            value: this.TimeInterval[1],
          });
        }
      }
      console.log("查询入参：", this.search);
      //debugger
      listarticlepages(this.search)
        .then((responseData) => {
          if (responseData.code == 100) {
            this.patientTotal = responseData.data.total;
            this.articlelist = responseData.data.rows;
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
};
</script>
