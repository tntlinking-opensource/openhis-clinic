<template>
<el-car>
  <el-row>
    <el-col :span="24" style="height:40px;">
      <blmb-form ref='blmbForm' @typeclick="typeclickload"></blmb-form>
      <el-button type="primary" style="float:right;margin-right:4.5%;" icon='el-icon-plus'  @click.native="onCreatePatient('')">新增</el-button>
    </el-col>
    <el-col :span="24">
      <el-form :inline="true" :model="blmbcxrc" class="demo-form-inline">
      <el-form-item label="模板名称" style="margin-right: 3px;">
    <el-input v-model="blmbcxrc.mbmc" placeholder="请输入模板名称"></el-input>
  </el-form-item>
   <el-form-item label="创建人" style="margin-right: 3px;">
    <el-input v-model="blmbcxrc.cjr" placeholder="请输入创建人姓名"></el-input>
  </el-form-item>
  <el-form-item label="模板类型"  style="margin-right: 3px;">
  <el-select v-model="blmbcxrc.mblx" placeholder="模板类型">
      <el-option label="全部" value="qb"></el-option>
      <el-option label="通用" value="0"></el-option>
      <el-option label="个人" value="1"></el-option>
    </el-select>
  </el-form-item>
  <el-form-item label="病历类型"  style="margin-right: 3px;">
    <el-select v-model="blmbcxrc.bllx" placeholder="病历类型">
      <el-option label="全部" value="qb"></el-option>
      <el-option label="西医病历" value="0"></el-option>
      <el-option label="中医病历" value="1"></el-option>
    </el-select>
  </el-form-item>
    <el-form-item style="margin-right: 3px;">
    <el-button type="primary" @click="onSubmit">查询</el-button>
    <el-button type="info" icon="el-icon-refresh-left" @click='resetCondition' :plain='true'>重置</el-button>
    <!-- <el-button @click="show3 = !show3">{{show3==true?"收起":"展开"}}</el-button> -->
  </el-form-item>
      </el-form>
    </el-col>

    <el-col :span="24">
      <blmb-form ref='blmbForm' @typeclick="typeclickload"></blmb-form>
      
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
      prop="mbbm"
      v-if="show"
      label="模板编码">
    </el-table-column>
    <el-table-column
      prop="mbmc"
      label="模板名称">
    </el-table-column>
    <el-table-column
      prop="mblx"
      label="模板类型">
    </el-table-column>
    <el-table-column
      prop="bllx"
      label="病历类型">
    </el-table-column>
    <el-table-column
      prop="cjrq"
      label="创建日期">
    </el-table-column>
    <el-table-column
      prop="cjr"
      label="创建人">
    </el-table-column>
    <el-table-column
      fixed="right"
      label="操作"
      width="100">
      <template slot-scope="scope">
        <el-button @click.native="onCreatePatient(scope.row)" type="text" size="small">编辑</el-button>
        <el-button type="text" size="small" @click="onDeleteRecipetemplate(scope.$index, scope.row)" >删除</el-button>
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
import BlmbForm from './blmbForm'
import MainUI from '@/views/components/mainUI'
import History from '@/views/components/history'
import {getblmblist,deletembbm} from '@/api/outpatient/blmb'
  export default {
    extends: MainUI,
  components: { 
    BlmbForm,
    History
  },

    data() {
      return {
        show:false,
        currentPage: 1,
        patientTotal: 0,
      patientList: [],
      allTotal:{},
      blmbcxrc: {
        limit: 20,
        offset: 0,
        companyId: currentUser.company.id,
        mbmc:'',
        cjr:'',
        mblx:'',
        bllx:'',
        mbbm:'',
        updatedBy:'',
        updatedTime:'',
      },
      

        tableData: [],
      }
    },
    methods:{
      onSizeChange(val) {
      this.currentPage = 1
      this.blmbcxrc.limit = val;
      this.blmbcxrc.offset = (this.currentPage - 1) * val
      this.Getblmbtable();
    },
    // onSizeChange(val) {
    //   this.currentPage = 1
    //   this.blmbcxrc.limit = val;
    //   this.blmbcxrc.offset = (this.currentPage - 1) * val
    //   this.Getblmbtable();
    // },
    onCurrentChange(val) {
      this.blmbcxrc.offset = (val - 1) * this.blmbcxrc.limit
      this.currentPage = val
      this.Getblmbtable();
    },
    blmbindexMethod(index){
       return (this.currentPage-1)*this.blmbcxrc.limit+index +1;
    },
    resetCondition() {
        this.blmbcxrc={
        limit: 20,
        offset: 0,
        companyId: currentUser.company.id,
        mbmc:'',
        cjr:'',
        mblx:'',
        bllx:'',
        mbbm:'',
        updatedBy:'',
        updatedTime:'',
      }
      this.Getblmbtable();
      },
      handleClick(row) {
        
      },
      onCreatePatient(types) {
      this.$refs.blmbForm.$emit('openAddworkbenchDialog',types);
    },
    typeclickload(){
      this. Getblmbtable();
    },
    Getblmbtable(){
      this.tableData=[];
      this.blmbcxrc.mblx=this.blmbcxrc.mblx=="qb"?"":this.blmbcxrc.mblx;
    this.blmbcxrc.bllx=this.blmbcxrc.bllx=="qb"?"":this.blmbcxrc.bllx;
    this.blmbcxrc.mbmc=this.blmbcxrc.mbmc;
    this.blmbcxrc.cjr=this.blmbcxrc.cjr;
      getblmblist(this.blmbcxrc).then((responseData)=>{
         if (responseData.code == 100) {
           this.patientTotal=responseData.data.total;
           if(responseData.data.total>0){
             responseData.data.rows.forEach(item => {
               if(item.mblx=="0"){item.mblx="通用"}else if(item.mblx=="1"){item.mblx="个人"}else{{item.mblx=""}}
               if(item.bllx=="0"){item.bllx="西医病历"}else if(item.bllx=="1"){item.bllx="中医病历"}else{{item.bllx=""}}
               this.tableData.push({
                    mbbm:item.mbbm,
                    mbmc: item.mbmc,
                    mblx: item.mblx,
                    bllx: item.bllx,
                    cjrq: item.createdTime,
                    cjr: item.cjr,
                   
               })
             });
           }
              }
      })
      .catch((error) => {
          this.$message.error(error);
        });
    },
    onSubmit() {
        this.Getblmbtable();
      },
    onDeleteRecipetemplate(index, row) {
      this.blmbcxrc.updatedBy=currentUser.name+"("+currentUser.loginname+")";
      this.blmbcxrc.mbbm=row.mbbm;
      this.$confirm('确定删除吗？', '确认', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        deletembbm(this.blmbcxrc).then(responseData => {
          if(responseData.code == 100) {
            this.showMessage({type: 'success', msg: '删除成功'})
          } else {
            this.showMessage(responseData)
          }
          this.Getblmbtable();
        }).catch(error => {
          this.outputError(error)  
        })
      }).catch(() => {})
    },
    },
    mounted(){
      this.Getblmbtable();
    }
  }
</script>