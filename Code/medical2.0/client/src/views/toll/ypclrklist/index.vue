<template>
<el-card class="page-container" style="padding: 0px;">
  <el-row>
    <el-col style="text-align:right;margin-bottom:10px">
      <el-button type="primary" icon="el-icon-upload2" @click='exportExcel'>导出</el-button>
    </el-col>
  </el-row>
<el-row>
  <el-col :span="24">
    <div style="margin:0 0 10px 0;">
    <el-radio-group v-model="YpjxcRc.ypcltype" size="mini" @change="orderbyChange">
      <el-radio-button label="1">药品</el-radio-button>
      <el-radio-button label="2">材料</el-radio-button>
      
    </el-radio-group>
  </div>
  </el-col>
   <el-col :span="24">
     <el-form :inline="true" :model="formInline" class="demo-form-inline">
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
    <el-form-item label="类型"  style="margin-right: 3px;">
      <el-select v-model="formInline.lx">
            <el-option v-for="item in formInline.options" :key="item.value" :label="item.label" :value="item.value"></el-option>
          </el-select>
    <!-- <el-select v-model="formInline.lx" placeholder="类型">
      <el-option label="全部" value="qb"></el-option>
      <el-option label="西药" value="medicalType_0"></el-option>
      <el-option label="中草药" value="medicalType_1"></el-option>
      <el-option label="中成药" value="medicalType_2"></el-option>
    </el-select> -->
  </el-form-item>
  <el-form-item label="状态"  style="margin-right: 3px;">
    <el-select v-model="formInline.zt" placeholder="状态">
      <el-option label="全部" value="qb"></el-option>
      <el-option label="停用" value="0"></el-option>
      <el-option label="启用" value="1"></el-option>
    </el-select>
  </el-form-item>

  <el-form-item label="批号" style="margin-right: 3px;">
    <el-input v-model="formInline.ph" placeholder="请输入批号"></el-input>
  </el-form-item>
    <el-form-item label="商品名" style="margin-right: 3px;">
    <el-input v-model="formInline.spm" placeholder="请输入商品名"></el-input>
  </el-form-item>

      <!-- <el-collapse-transition style="margin-right: 5px;">
        <div v-show="show3">
          <el-form-item label="批号" style="margin-right: 5px;">
    <el-input v-model="formInline.ph" placeholder="请输入批号"></el-input>
  </el-form-item>
    <el-form-item label="商品名" style="float:left;margin-right: 5px;">
    <el-input v-model="formInline.spm" placeholder="请输入商品名"></el-input>
  </el-form-item>
        </div>
      </el-collapse-transition> -->
  
  <!-- <el-form-item label="批号">
    <el-input v-model="formInline.ph" placeholder="请输入批号"></el-input>
  </el-form-item>
    <el-form-item label="商品名">
    <el-input v-model="formInline.spm" placeholder="请输入商品名"></el-input>
  </el-form-item> -->
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
    ref="tableData"
    border
    height="calc(100vh - 330px)"
    :summary-method="getSummaries"
    show-summary
   >
    <el-table-column
             label="序号"
              type="index"
              :index="indexMethod"
              align="center">
            </el-table-column>
    <el-table-column
    width="150px"
    align="center"
      prop="rkrq"
      label="入库日期">
    </el-table-column>
    <el-table-column
    width="150px"
    align="center"
      prop="rkdh"
      label="入库单号">
    </el-table-column>
    <el-table-column
    width="150px"
    align="center"
      prop="rkry"
      label="入库人员">
    </el-table-column>
    <el-table-column
    align="center"
      prop="spbh"
      label="商品编码">
    </el-table-column>
    <el-table-column
    width="150px" 
      align="center"
      prop="spmc"
      label="商品名称">
    </el-table-column>
    <el-table-column
    width="200px" 
    align="center"
      prop="lx"
      :formatter="stateFormat"
      label="分类">
    </el-table-column>
    <el-table-column
    width="120px"
      prop="ph"
      label="批号">
    </el-table-column>
    <el-table-column
      prop="gg"
      width="150px" 
      align="center"
      label="规格">
    </el-table-column>
    <el-table-column
      prop="sccj"
      label="生产厂家">
    </el-table-column>
    <el-table-column
    align="center"
      prop="gys"
      label="供应商">
    </el-table-column>
    <el-table-column
    width="150px" 
    align="center"
      prop="sl"
      label="数量">
    </el-table-column>
    <el-table-column
    width="150px" 
    align="center"
      prop="dw"
      label="单位">
      </el-table-column>
    <el-table-column
    width="150px" 
    align="center"
      prop="lsj"
      label="零售价">
    </el-table-column>
    <el-table-column
    width="150px" 
    align="center"
      prop="cbj"
      label="成本价">
    </el-table-column>
    <el-table-column
    width="150px" 
    align="center"
      prop="cbhj"
      label="成本合计">
    </el-table-column>
    
    <el-table-column
    width="200px" 
    align="center"
      prop="yxq"
      label="有效期">
    </el-table-column>
    
    <el-table-column
    width="150px" 
    align="center"
      prop="shzt"
      :formatter="shztFormat"
      label="审核状态">
    </el-table-column>
    <el-table-column
    width="150px" 
    align="center"
      prop="bz"
      label="备注">
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
import { getypclrkcxlist,getypclrkcxsums,exportDrugOrStuffStock } from "@/api/toll/tollInfo";
import { BigNumber } from "bignumber.js";

  export default {

    data() {
      return {
        show3: false,
              //时间选择
      TimeInterval:this.getInitializeDate(),
      formInline: {
          spm: '',
          zt: '',
          lx:'',
          ph:'',
          options:[
            {
            label: '西药',
            value: 'medicalType_0',
            type: '1'
          },
          {
            label: '中草药',
            value: 'medicalType_1',
            type: '1'
          },
          {
            label: '中成药',
            value: 'medicalType_2',
            type: '1'
          },
          ],
          option:[{
            label: '医用材料',
            value: 'stuffType_0',
            type: '2'
          },
          {
            label: '非医用材料',
            value: 'stuffType_1',
            type: '2'
          },
          {
            label: '西药',
            value: 'medicalType_0',
            type: '1'
          },
          {
            label: '中草药',
            value: 'medicalType_1',
            type: '1'
          },
          {
            label: '中成药',
            value: 'medicalType_2',
            type: '1'
          },
          ]
        },
        

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
        
        tableData: [],

        YpjxcRc: {
        limit: 20,
        offset: 0,
        companyId: currentUser.company.id,
        kssj:"",
        jssj:"",
        lx:"",
        zt:"",
        spm:"",
        ph:"",
        orderby:"",
        ypcltype:"1",
      },
      currentPage: 1,
      patientTotal: 0,
      patientList: [],
      allTotal:{},

      };
    },
    updated() {
    this.$nextTick(() => {
      this.$refs['tableData'].doLayout();
    })
  },
    methods: {
        //导出
        exportExcel(){
            exportDrugOrStuffStock(this.YpjxcRc).then((res)=>{
              const filename = decodeURI(res.headers.split(';')[1].split('=')[1]) || '.xls'
                      const blob = new Blob([res.data], {

                      type: 'application/octet-stream'

                    })

                      let url = window.URL.createObjectURL(blob);

                      let link = document.createElement('a');

                      link.style.display = 'none';

                      link.href = url;

                      link.setAttribute('download', filename);

                      document.body.appendChild(link);

                      link.click()
            }).catch((error)=>{
              this.outputError(error)
            })
          },


      stateFormat(row, column) {
        if (row.lx =="stuffType_0") {
          return '医用材料'
        }
        else if(row.lx =="stuffType_1"){
          return '非医用材料'
        }
        else if (row.lx =="medicalType_0") {
          return '西药'
        }
        else if(row.lx =="medicalType_1"){
          return '中草药'
        }
        else if(row.lx =="medicalType_2"){
          return '中成药'
        } else  {
          return ''
        }
      },
      shztFormat(row, column) {
        if (row.shzt =="supplierStorageExamineStatus_0") {
          return '通过'
        }
        else if(row.shzt =="supplierStorageExamineStatus_1"){
          return '不通过'
        }
        else if (row.shzt =="supplierStorageExamineStatus_2") {
          return '已作废'
        } else  {
          return ''
        }
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
      getSummaries(param) {
        const { columns, data } = param;
        const sums = [];
        columns.forEach((column, index) => {
          if (index === 0) {
            sums[index] = '总价';
            return;
          }
          // else if (index === 8 || index ===9 ||index >11){
          //   const values = data.map(item => Number(item[column.property]));
          // if (!values.every(value => isNaN(value))) {
          //   sums[index] = values.reduce((prev, curr) => {
          //     const value = Number(curr);
          //     if (!isNaN(value)) {
          //       return prev + curr;
          //     } else {
          //       return prev;
          //     }
          //   }, 0);
          //   sums[index] += '';
          // } 
          
          // }
          else {
            sums[index] = '';
          }
          
        });
        // sums[11] = new BigNumber(Number(this.allTotal.sl)).toFormat(2)
        // sums[13] = new BigNumber(Number(this.allTotal.lsj)).toFormat(2)+'元'
        // sums[14] = new BigNumber(Number(this.allTotal.cbj)).toFormat(2)+'元'
        sums[15] = new BigNumber(Number(this.allTotal.cbhj)).toFormat(2)+'元'
        
        return sums;
      },
       onSizeChange(val) {
      this.currentPage = 1
      this.YpjxcRc.limit = val;
      this.YpjxcRc.offset = (this.currentPage - 1) * val
      this.Getypjxclist();
    },
    onCurrentChange(val) {
      this.YpjxcRc.offset = (val - 1) * this.YpjxcRc.limit
      this.currentPage = val
      this.Getypjxclist();
    },
    indexMethod(index){
       return (this.currentPage-1)*this.YpjxcRc.limit+index +1;
    },
     resetCondition(){
      this.YpjxcRc= {
        limit: 20,
        offset: 0,
        companyId: currentUser.company.id,
        kssj:"",
        jssj:"",
        lx:"",
        zt:"",
        spm:"",
        ph:"",
        orderby:"",
        ypcltype:"1",
      },
      this.formInline= {
          spm: '',
          zt: '',
          lx:'',
          ph:'',
          options:[
            {
            label: '西药',
            value: 'medicalType_0',
            type: '1'
          },
          {
            label: '中草药',
            value: 'medicalType_1',
            type: '1'
          },
          {
            label: '中成药',
            value: 'medicalType_2',
            type: '1'
          },
          ],
          option:[{
            label: '医用材料',
            value: 'stuffType_0',
            type: '2'
          },
          {
            label: '非医用材料',
            value: 'stuffType_1',
            type: '2'
          },
          {
            label: '西药',
            value: 'medicalType_0',
            type: '1'
          },
          {
            label: '中草药',
            value: 'medicalType_1',
            type: '1'
          },
          {
            label: '中成药',
            value: 'medicalType_2',
            type: '1'
          },
          ]
        },
      this.TimeInterval=this.getInitializeDate(),
      this.Getypjxclist();
     },
     optionsdata(params){
       this.formInline.options=[];
        let options = this.formInline.option
        let res = []
        for (let i = 0; i < options.length; i++) {
          if(params == options[i].type){
            res.push(options[i])
          }
        }

        this.formInline.options = res
      },
orderbyChange(){
  var txt = this.YpjxcRc.ypcltype;
        this.optionsdata(txt);
this.Getypjxclist();
},
    Getypjxclist(){
      this.tableData=[];
  if(this.TimeInterval){
    this.YpjxcRc.kssj=this.TimeInterval[0];
  this.YpjxcRc.jssj=this.TimeInterval[1]
  }
  this.YpjxcRc.zt=this.formInline.zt=="qb"?"":this.formInline.zt;
    this.YpjxcRc.lx=this.formInline.lx=="qb"?"":this.formInline.lx;
this.YpjxcRc.spm=this.formInline.spm;
this.YpjxcRc.ph=this.formInline.ph;
this.YpjxcRc.ypcltype=this.YpjxcRc.ypcltype;
getypclrkcxlist(this.YpjxcRc).then((responseData)=>{
if (responseData.code == 100){
  this.patientTotal=responseData.data.total;
  if(responseData.data.total>0){
  responseData.data.rows.forEach((item)=>{
    this.tableData.push({
      rkrq:item.rkrq,
      rkdh:item.rkdh,
      rkry:item.rkry,
      spbh:item.spbh,
      spmc:item.spmc,
      gg:item.gg,
      sccj:item.sccj,
      gys:item.gys,
      sl:Math.floor(item.sl/item.zj) >= 0 ? Math.floor(item.sl/item.zj)+(item.dw)+((item.sl%item.zj > 0) ? (item.sl%item.zj) + item.zxdw:""):item.sl+item.zxdw,
      dw:item.dw,
      lsj:item.lsj,
      cbj:item.cbj,
      cbhj:item.cbhj,
      ph:item.ph,
      yxq:item.yxq,
      lx:item.lx,
      shzt:item.shzt,
    })
  })
  }
getypclrkcxsums(this.YpjxcRc).then((ref)=>{
  if(ref.data.total>0){
   this.allTotal=ref.data.rows[0];
  }
})
}

})
    },
    onSubmit() {
        this.Getypjxclist();
        
      },
    },
    
    mounted(){
      this.Getypjxclist();
    }
  };
</script>
<style lang="scss">
.el-picker-panel__footer .el-picker-panel__link-btn.el-button--text {
  display: none;
}

.el-card__body{
  padding: 0px;
}
</style>