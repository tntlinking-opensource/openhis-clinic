<template>
  <el-row v-loading='loading'>
    <!-- 历史记录  -->
    <History :bussObject='curentRow' ></History>
    <!-- 编辑窗口  -->
    <inspectionCheck-form ref='inspectionCheckForm' :permission='permission' v-on:save-finished='getInspectionCheckList()'></inspectionCheck-form>
    <div class="page-container">
        <!--  搜索栏  开始 -->
        <div class='query-form-container'>
          <el-row v-if='!moreCodition' class='search-row'>
            <el-form :model='queryModel' @submit.native.prevent label-position="left" label-width='70px' ref='queryForm' :inline-message='true'>
                      <el-col :span="6">
                      <el-form-item label='开单时间' prop='completeDate'>
                       <el-date-picker
                        v-model="queryModel.completeDate"
                        type="datetimerange"
                        align="right"
                        start-placeholder="开始日期"
                        end-placeholder="结束日期"
                        value-format="yyyy-MM-dd HH:mm:ss"
                        format="yyyy-MM-dd HH:mm:ss"
                        :default-time="['00:00:00', '23:59:59']">
                      </el-date-picker>
                      </el-form-item>
                    </el-col>
                      <el-col :span="6">
              <el-form-item label='患者姓名' prop='patientName'>
                <el-input v-model='queryModel.patientName' :clearable='true' placeholder='请输入患者姓名'></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item label='开单医生' prop='completeBy'>
                 <el-select  v-model="queryModel.completeBy" placeholder="请选择">
                        <el-option
                          v-for="item in doctorList"
                          :key="item.name"
                          :label="item.name"
                          :value="item.name">
                        </el-option>
                      </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item label='状态' prop='status'>
                 <el-select  v-model="queryModel.status" placeholder="请选择">
                        <el-option
                          v-for="item in statusList"
                          :key="item.value"
                          :label="item.name"
                          :value="item.value">
                        </el-option>
                      </el-select>
              </el-form-item>
            </el-col>
               <el-col :span="3" style="text-align:right;padding-right:5px;float:right;">
               <el-button
                    type="primary"
                    icon="el-icon-search"
                    @click="onSearch()"
                    :plain="true"
                  >搜索</el-button>
                <el-button
                    type="info"
                    icon="el-icon-refresh-left"
                    @click="reset"
                    :plain="true"
                  >重置</el-button>
            </el-col>
            </el-form>
          </el-row>
          <QueryForm v-else v-model='moreParm' :tableId='tableId' :schemeId='schemeId'  :routerId='$route.meta.routerId' @search='onSearch()' @moreCodition='onMoreCodition()'></QueryForm>
        </div>
        <!--  搜索栏  结束 -->

        <!-- 工具栏 开始 -->
        <!-- <div class="page-container-header-end">
          <div>
            <el-button v-show='permission.add' type='primary' icon='el-icon-plus' :plain='true' @click='onCreateInspectionCheck()'>添加</el-button>
          </div>
        </div> -->
        <!-- 工具栏 结束 -->

      <!-- 表格栏  开始 -->
      <el-row>
        <el-col :span='24'>
          <div @mouseleave='moveTableOutside'>        
            <el-table class='drag_table' ref="tableRef" height="calc(100vh - 200px)" :data='inspectionCheckList' border @sort-change='onSortChange' @header-dragend='onChangeWidth' :cell-class-name='cellClassName' :header-cell-class-name='headerCellClassName' highlight-current-row>                
              <el-table-column
              label="序号"
              type="index"
              :index="indexMethod"
              align="center">
            </el-table-column>
              <el-table-column v-for="(cv, index) in columnViews" v-if='cv.display' :prop='cv.prop' :key="`columnViews_${index}`" :label='cv.label' sortable='custom' :align='cv.align' :min-width='cv.miniWidth+"px"' :width='cv.width+"px"' header-align='center' :column-key='index.toString()' :render-header="renderHeader">
                <template slot-scope='{row,$index}'>
                  <span v-if='columnViews[index].showType == "Switch" || columnViews[index].showType == "Checkbox" || columnViews[index].showType == "Radio"'>
                    <li v-if='getAttrValue(row, columnViews[index].prop) == "1"' class='el-icon-check' style='color:#F56C6C;'></li>
                  </span>
                  <span v-else-if="columnViews[index].prop=='type'">
                    <span v-if="getAttrValue(row, columnViews[index].prop)=='0'">检验</span>
                    <span v-else>检查</span>
                  </span>
                  <span v-else-if="columnViews[index].prop=='status'">
                    <span v-if="getAttrValue(row, columnViews[index].prop)=='0'" style="color:#FF9966">待填写</span>
                    <span v-else style="color:#99CC66">已填写</span>
                  </span>
                  <span  v-else-if="columnViews[index].prop=='sex'">
                    <span v-if="getAttrValue(row, columnViews[index].prop)=='gender_1'">女</span>
                    <span v-else>男</span>
                  </span>
                  <span v-else>{{ getAttrValue(row, columnViews[index].prop, columnViews[index].javaType )}}</span>

                </template>
              </el-table-column>
              <!--表行级操作按钮-->
              <el-table-column label='操作' header-align='center' :key="Math.random()" :width='120 + "px"' fixed="right">        
                <template slot='header' slot-scope="scope">
                  <span>操作</span>
                  <view-columns-select v-model='columnViews' v-on:save-column-view='saveColumn' v-on:show-all-column='showAllColumn' v-on:show-default-column='showDefaultColumn'></view-columns-select>
                  <export-excel-button v-show='permission.export' :data='inspectionCheckList' :tHeader='getHeads()' :filterVal='getFilterVal()' :plain='true'></export-excel-button>
                </template>
                <template slot-scope='scope'>
                  <el-button type="text" v-if="scope.row.status=='0'"  @click="fill(scope.row,scope.$index,0)">填写报告</el-button>
                  <el-button type="text" v-if="scope.row.status=='1'"  @click="fill(scope.row,scope.$index,1)">查看详情</el-button>
                  <!-- <OperationIcon v-show='permission.view' type='info' content='查看' placement='top-start' icon-name='el-icon-view' 
                    @click='onViewInspectionCheck(scope.$index, scope.row)'></OperationIcon>
                  <OperationIcon v-show='permission.edit' type='primary' content='编辑' placement='top-start' icon-name='el-icon-edit' 
                    @click='onEditInspectionCheck(scope.$index, scope.row)'></OperationIcon>
                  <OperationIcon v-show='permission.add' type='primary' content='复制' placement='top-start' icon-name='el-icon-document' 
                    @click='onCopyInspectionCheck(scope.$index, scope.row)'></OperationIcon>
                  <OperationIcon v-show='permission.remove' type='danger' content='删除' placement='top-start' icon-name='el-icon-delete' 
                    @click='onDeleteInspectionCheck(scope.$index, scope.row)'></OperationIcon>
                  <OperationIcon v-show='permission.view' type='info' content='历史记录' placement='top-start' icon-name='el-icon-info' 
                    @click='onShowHistory(scope.$index, scope.row)'></OperationIcon> -->
                </template>
              </el-table-column>
            </el-table>
    	  </div>	          
        </el-col>
      </el-row>
      <!-- 表格栏  结束 -->
      <!-- 分页栏     开始 -->
      <el-row>
        <el-col :span='24'>
          <el-pagination
            background     
            @size-change='onSizeChange'
            @current-change='onCurrentChange'
            :current-page.sync='currentPage'
            :page-sizes='[20, 50, 100, inspectionCheckTotal]'
            :page-size='20'
            layout='total, sizes, prev, pager, next, jumper'
            :total='inspectionCheckTotal'>
          </el-pagination>
        </el-col>
      </el-row>
      <!-- 分页栏     结束 -->     </div>

      <!-- 填写报告弹出框 -->
      <el-dialog
        :title="typeName"
        :visible.sync="dialogVisible"
        v-if="dialogVisible"
        width="75%"
        :before-close="handleClose">
            <div slot="title" class="dialog-header">
          {{ typeName }}
          <OperationIcon
          v-show="flags"
            type="primary"
            text="编辑"
            placement="top-start"
            icon-name="el-icon-edit"
            @click="switchEdits"
          ></OperationIcon>
        </div>
         <div v-if="this.type=='0'">
            <el-form :model='bizFormModel.inspectionCheckInfo' :rules='formRules' 
              ref='bizFormModel' label-width='120px' label-position='right' class='edit-form'> 
           <!-- <el-card class="box-card main-card"> -->
            
              <div style="margin-left:-60px">  
                      <el-row >
                <el-col :span='24/4'>
                  <el-form-item label='姓名' prop='name' >
                    <el-input :disabled='true' v-model='bizFormModel.inspectionCheckInfo.name' :maxlength='120'  >
                    </el-input>
                  </el-form-item>
                </el-col>
                <el-col :span='24/4'>
                  <el-form-item label='采样时间' prop='samplingDate' >
                    <!-- <el-input :disabled='true' v-model='bizFormModel.samplingDate'></el-input> -->
                    <el-date-picker
                      :disabled="flags"
                      v-model="bizFormModel.inspectionCheckInfo.samplingDate"
                      type="datetime"
                      placeholder="选择日期时间"
                      value-format="yyyy-MM-dd HH:mm:ss"
                      format="yyyy-MM-dd HH:mm:ss">
                    </el-date-picker>
                  </el-form-item>
                </el-col>
                 <el-col :span='24/4'>
                  <el-form-item label='样本号' prop='samplingNum' >
                    <el-input placeholder="请输入内容" :disabled="flags" v-model='bizFormModel.inspectionCheckInfo.samplingNum' :maxlength='100'></el-input>     
                  </el-form-item>
                </el-col>
                 <el-col :span='24/4'>
                  <el-form-item label='检验仪器' prop='instrument' >
                    <el-input placeholder="请输入内容" :disabled="flags" v-model='bizFormModel.inspectionCheckInfo.instrument' :maxlength='100'></el-input>     
                  </el-form-item>
                </el-col>
                </el-row>
                <el-row>
                 <el-col :span='24/4'>
                  <el-form-item label='检验号' prop='checkoutNum' >
                    <el-input placeholder="请输入内容" :disabled="flags" v-model='bizFormModel.inspectionCheckInfo.checkoutNum' :maxlength='100'></el-input>     
                  </el-form-item>
                </el-col>
                <el-col :span='24/4'>
                  <el-form-item label='检验时间' prop='checkoutDate' >
                    <el-date-picker
                      :disabled="flags"
                      v-model="bizFormModel.inspectionCheckInfo.checkoutDate"
                      type="datetime"
                      placeholder="选择日期时间"
                      value-format="yyyy-MM-dd HH:mm:ss"
                      format="yyyy-MM-dd HH:mm:ss">
                    </el-date-picker>    
                  </el-form-item>
                </el-col>
                 <el-col :span='24/4'>
                  <el-form-item label='检验科室' prop='checkoutOffice' >
                    <el-input placeholder="请输入内容" :disabled="flags" v-model='bizFormModel.inspectionCheckInfo.checkoutOffice' :maxlength='100'></el-input>     
                  </el-form-item>
                </el-col>
              </el-row>
              </div>
            
            <!-- </el-card> -->
            <br>
             <el-table
              :data="bizFormModel.inspectionCheckInfo.inspectionCheckDetails"
              style="width: 100%">
               <el-table-column
                type="index"
                width="50"
                label="序号">
              </el-table-column>
              <el-table-column
                prop="costItem.itemName"
                label="项目名称"
                width="width">
              </el-table-column>
              <el-table-column
                prop="conclusion"
                label="结果"
                width="width">
                <template slot-scope="scope">
                  <el-input placeholder="请输入内容" :disabled="flags" v-model='scope.row.conclusion' :maxlength='100'  >
                    </el-input>
                </template>
              </el-table-column>
               <el-table-column
                prop="sign"
                label="高低标记"
                width="width">
                <template slot-scope="scope">
                     <el-select :disabled="flags" v-model="scope.row.sign" placeholder="请选择">
                        <el-option
                          v-for="item in signList"
                          :key="item.value"
                          :label="item.name"
                          :value="item.value">
                        </el-option>
                      </el-select>
                </template>
              </el-table-column>
              <el-table-column
                prop="costItem.costItemPackages[0].referenceValue"
                label="参考标记"
                width="width">
                <template slot-scope="scope">
                  <!-- {{scope.row.costItemPackages[0].referenceValue.split("\n")}} -->
                  <!-- <el-input :disabled="true" type="textarea" v-model='scope.row.costItem.costItemPackages[0].referenceValue'   >
                    </el-input> -->
                     <span v-if="scope.row.costItem.costItemPackages.length>0">
                        <el-input :disabled="true" type="textarea" v-model='scope.row.costItem.costItemPackages[0].referenceValue'   >
                    </el-input>
                     </span>
                    
                </template>
              </el-table-column>
              <el-table-column
                prop="costItem.costItemPackages[0].referenceUnit"
                label="单位"
                width="width">
              </el-table-column>
             
               <el-table-column
                prop="describes"
                label="描述"
                width="width">
                <template slot-scope="scope">
                  <el-input :disabled="flags" v-model='scope.row.describes' :maxlength='100'  >
                    </el-input>
                </template>
              </el-table-column>
            </el-table>
            <br>
            <div style="margin-left:-50px">
              <el-form-item label='报告结论' prop='reportConclusion' >
                <el-input
                :disabled="flags"
                  type="textarea"
                   :autosize="{ minRows: 2, maxRows: 100}"
                  placeholder="请输入内容"
                  v-model="bizFormModel.inspectionCheckInfo.reportConclusion">
                </el-input>
            </el-form-item>
            </div>
            <br>
           <!-- <el-form-item label="" prop="uploadFile">
              <el-upload
                class="avatar-uploader"
                 action="#"
                :show-file-list="switchEdit==0"
                :on-change="handleLicensePreview"
                :before-upload="beforeLicenseUpload"
                :on-remove="handleRemove"
                :auto-upload="false"
                :on-exceed="handleExceed"
                :limit="1"
                :on-error="hanldeError"
                :on-preview="handlePreview"
                accept=".jpg,.jpeg,.png,.pdf,.JPG,.JPEG"

              >          
             
                <el-button v-if="photoId==null||photoId=='' || switchEdit==0" slot="trigger" size="mini" type="primary">上传文件</el-button>           
              </el-upload>
               <img v-if="bizFormModel.uploadFile" :src="bizFormModel.uploadFile" class="avatar">
               <i v-else class="el-icon-plus avatar-uploader-icon"></i> 
                  <el-button  size="mini" type="primary" v-if="bizFormModel.uploadFile && switchEdit==1"  @click="look(bizFormModel.uploadFile)">预览文件</el-button>
            </el-form-item> -->
            <el-form-item label='' prop='' >
              <div style="margin-left:-50px">
              <upload-file  :objectId="uploadFiles" @getFileList="getFileList" :action="flags" @deleteFile="deleteFile"></upload-file>
              </div>
            </el-form-item>
          </el-form>
          
         </div>
          <div v-if="this.type=='1'">
            <el-form :model='bizFormModel.inspectionCheckInfo' :rules='formRules' 
              ref='bizFormModel' label-width='120px' label-position='right' class='edit-form'> 
           <!-- <el-card class="box-card main-card"> -->
            
              <div style="margin-left:-60px">  
                      <el-row >
                <el-col :span='24/4'>
                  <el-form-item label='姓名' prop='name' >
                    <el-input :disabled='true' v-model='bizFormModel.inspectionCheckInfo.name' :maxlength='120'  >
                    </el-input>
                  </el-form-item>
                </el-col>
                <el-col :span='24/4'>
                  <el-form-item label='登记时间' prop='samplingDate' >
                    <!-- <el-input :disabled='true' v-model='bizFormModel.samplingDate'></el-input> -->
                    <el-date-picker
                      :disabled="flags"
                      v-model="bizFormModel.inspectionCheckInfo.samplingDate"
                      type="datetime"
                      placeholder="选择日期时间"
                      value-format="yyyy-MM-dd HH:mm:ss"
                      format="yyyy-MM-dd HH:mm:ss">
                    </el-date-picker>
                  </el-form-item>
                </el-col>
                 <el-col :span='24/4'>
                  <el-form-item label='检验设备' prop='instrument' >
                    <el-input :disabled="flags" placeholder="请输入内容" v-model='bizFormModel.inspectionCheckInfo.instrument' :maxlength='100'></el-input>     
                  </el-form-item>
                </el-col>
                <el-col :span='24/4'>
                  <el-form-item label='检查类型' prop='checkType' >
                    <el-input :disabled="flags" placeholder="请输入内容" v-model='bizFormModel.inspectionCheckInfo.checkType' :maxlength='100'></el-input>     
                  </el-form-item>
                </el-col>
                
                </el-row>
                <el-row>
                   <el-col :span='24/4'>
                  <el-form-item label='检查部位' prop='checkPart' >
                    <el-input :disabled="flags" placeholder="请输入内容" v-model='bizFormModel.inspectionCheckInfo.checkPart' :maxlength='100'></el-input>     
                  </el-form-item>
                </el-col>
                   <el-col :span='24/4'>
                  <el-form-item label='报告医师' prop='doctor' >
                    <el-input :disabled="flags" placeholder="请输入内容" v-model='bizFormModel.inspectionCheckInfo.doctor' :maxlength='100'></el-input>     
                  </el-form-item>
                </el-col>
                 <el-col :span='24/4'>
                  <el-form-item label='检查项目' prop='checkProject' >
                    <el-input :disabled="flags" placeholder="请输入内容" v-model='bizFormModel.inspectionCheckInfo.checkProject' :maxlength='100'></el-input>     
                  </el-form-item>
                </el-col>
                <el-col :span='24/4'>
                  <el-form-item label='报告时间' prop='checkoutDate' >
                    <el-date-picker
                      :disabled="flags"
                      v-model="bizFormModel.inspectionCheckInfo.checkoutDate"
                      type="datetime"
                      placeholder="选择日期时间"
                      value-format="yyyy-MM-dd HH:mm:ss"
                      format="yyyy-MM-dd HH:mm:ss">
                    </el-date-picker>    
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row>
                <el-col :span="24/1">
                  <el-form-item label='检查所见' prop='checkAdvise' >
                    <el-input type="textarea" width='100%' :disabled="flags" v-model='bizFormModel.inspectionCheckInfo.checkAdvise'  :maxlength='100'></el-input>     
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row>
                <el-col :span="24/1">
                  <el-form-item label='意见' prop='opinion' >
                    <el-input type="textarea" width='100%' :disabled="flags" v-model='bizFormModel.inspectionCheckInfo.opinion'  :maxlength='100'></el-input>     
                  </el-form-item>
                </el-col>
              </el-row>
              <!-- <el-form-item label="" prop="uploadFile">
              <el-upload
                class="avatar-uploader"
                 action="#"
                :show-file-list="switchEdit==0"
                :on-change="handleLicensePreview"
                :before-upload="beforeLicenseUpload"
                :on-remove="handleRemove"
                :auto-upload="false"
                :on-exceed="handleExceed"
                :limit="1"
                :on-error="hanldeError"
                :on-preview="handlePreview"
                accept=".jpg,.jpeg,.png,.pdf,.JPG,.JPEG"

              >   
                   
                <el-button v-if="photoId==null||photoId=='' || switchEdit==0" slot="trigger" size="mini" type="primary">上传文件</el-button>           
              </el-upload>
              <img v-if="bizFormModel.uploadFile" :src="bizFormModel.uploadFile" class="avatar">
               <i v-else class="el-icon-plus avatar-uploader-icon"></i>  
                  <el-button  size="mini" type="primary" v-if="bizFormModel.uploadFile && switchEdit==1"  @click="look(bizFormModel.uploadFile)">预览文件</el-button>
            </el-form-item> -->
             <el-form-item label='' prop='' >
              <div >
              <upload-file  :objectId="uploadFiles" @getFileList="getFileList" :action="flags" @deleteFile="deleteFile"></upload-file>
              </div>
            </el-form-item>
              </div>
            </el-form>
            </div>
        <span slot="footer" class="dialog-footer">
          <el-button v-if="!flags" @click="dialogVisible = false">取 消</el-button>
          <el-button type="primary" v-if="!flags" @click="save">保 存</el-button>
        </span>
      </el-dialog>

  </el-row>
</template>

<script>
import { validatenull } from '@/utils/validate'
import { getPhotoById,getFiled } from "@/api/sys/sysSeting"
import { listInspectionCheckPage, getInspectionCheckById, deleteInspectionCheck } from '@/api/cure/inspectionCheck'
import { listResourcePermission } from '@/api/admin/common/permission'
import {getInspectionCheckInfoByInspecId,saveInspectionCheckInfo} from '@/api/cure/inspectionCheckInfo'
import {getInspectionCheckDetailById} from '@/api/cure/inspectionCheckDetail'
import InspectionCheckForm from './inspectionCheckForm'
import ExportExcelButton from '@/components/ExportExcelButton'
import ViewColumnsSelect from '@/views/components/ViewColumnsSelect'
import {getCostItemById} from '@/api/treatment/costItem'
import QueryForm from '@/views/components/queryForm'
import MainUI from '@/views/components/mainUI'
import BaseUI from "@/views/components/baseUI";
import OperationIcon from '@/components/OperationIcon'
import History from '@/views/components/history'
import UploadFile from '../../components/uploadFile.vue'
let Base64 = require('js-base64').Base64
export default {
  extends: MainUI,BaseUI,
  components: { 
    InspectionCheckForm,
    ExportExcelButton,
    ViewColumnsSelect,
    QueryForm,
    OperationIcon,
    History,
    UploadFile
  },
  data() {
    return {
      permission: {
        view: false,
        add: false,
        edit: false,
        remove: false,
        export: false
      },
      queryTypes: {
        'name': 'like',
      },
      queryModel: {
        'patientName': '',   // 项目名称
        'completeDate':'',  //开单时间
        'status':'',    //状态
        'completeBy':'',  //开单医生
      },
      search: {
        params: [{columnName: 'company_id', queryType: '=', value: currentUser.company.id}],    
        offset: 0,
        limit: 20,
		columnName: '',      // 排序字段名
        order: ''            // 排序
      },
      switchEdit:0,  //编辑上传文件按钮
      currentPage: 1,
      inspectionCheckTotal: 0,
      inspectionCheckList: [],
      fileName:'',  //文件名称
      photoId:null,
      oprColumnWidth: 140,  // 操作列宽
      tableId: '1212102786706588488',
      schemeId: '1212102786706588513',
      type:"",//报告类型
      typeName:"",  //报告名称
      dialogVisible:false,
      bizFormModel:{
        fileIdFile:[],   //文件
        uploadFile:[],  //文件
        inspectionCheckInfo:{}
      },     //填写报告封装
      inspectionIndex:0,    //主表点击索引
      checkIndex:0,    //主表点击索引
      inspectionCheckTableData:[],   //诊疗项目报告封装
      conclusion:[],  //结果
      describe:[],    //描述
      sign:[],       //标记
      flags:false,
      doctorList:[],  //医生数组
      signList:[
        {
          name:'高',
          value:1,
        },{
          name:'正常',
          value:0
        },{
          name:'低',
          value:2
        }
      ],
      statusList:[
        {
          name:'待填写',
          value:'0'
        },
        {
          name:'已填写',
          value:"1"
        }
      ],
      uploadFiles:'',
      fileIds:[],
      flages:false,  //防止重复提交
    }
  },
  methods: {
    //重置
    reset(){
       this.queryModel={
        'patientName': '',   // 项目名称
        'completeDate':'',  //开单时间
        'status':'',    //状态
        'completeBy':'',  //开单医生
      }
      this.getInspectionCheckList()
    },
    getFileList(fileList){
      console.log(fileList,'撒娇发生了警方破案就');
      this.bizFormModel.fileIdFile=[]
      this.bizFormModel.uploadFile=[]
      this.bizFormModel.uploadFile=fileList
      for (let i = 0; i < fileList.length; i++) {
       this.bizFormModel.fileIdFile.push(fileList[i].raw)
      }
     
      console.log(this.bizFormModel.uploadFile,'撒娇发生了警方破案就');
      console.log(this.bizFormModel.fileIdFile,'按法律框架老咔叽分类');
    },
    deleteFile(fileIds){
      this.fileIds.push(fileIds)
      console.log(this.fileIds,'删除看');
    },
    //编辑
    switchEdits(){
      this.flags=false
      this.switchEdit=0
    },
    
    //预览
    look(row){
     
      //  var blob = this.dataURLtoBlob(row)
      //   const elink = document.createElement('a')
      //   // 设置下载文件名
      //   const timedate = Date.parse(new Date())
      //   elink.download = this.fileName
      //   elink.style.display = 'none'
      //   elink.href = URL.createObjectURL(blob)
      //   document.body.appendChild(elink)
      //   elink.click()
      //   document.body.removeChild(elink)
     window.open("http://192.168.0.31:8099/ffview/onlinePreview?url=" + encodeURIComponent(Base64.encode(row)));
    },
    // 将base64转换为blob
    dataURLtoBlob: function (dataurl) {
      var arr = dataurl.split(',')
      var mime = arr[0].match(/:(.*?);/)[1]
      var bstr = atob(arr[1])
      var n = bstr.length
      var u8arr = new Uint8Array(n)
      while (n--) {
        u8arr[n] = bstr.charCodeAt(n)
      }
      return new Blob([u8arr], { type: mime })
    },
    getPhoto(id) {
      if(!id){
        this.bizFormModel.uploadFile = ''
      }else{
        getFiled(id).then((res) => {
          console.log(res,'就是看');
         const src = `data:text/plain;base64,${res.base64Str}`;
        // let name = res.name.substring(0,res.name.lastIndexOf("."))
        let name = res.name
        this.fileName=name
        this.bizFormModel.uploadFile = src;
        console.log(this.fileName, ".......");
        return src;
      });
      }
    },
    save(){
     if(this.flages){
       return
     }
     this.flages=true
      console.log(this.bizFormModel.fileIdFile,'萨拉时间分厘卡');
    //  let userHeaderFile = new FormData();
      //  userHeaderFile.append("entity", JSON.stringify(this.bizFormModel.inspectionCheckInfo));
      // userHeaderFile.append("deleteIds",JSON.stringify([this.photoId]));
     // return
       this.setLoad()
     if(this.bizFormModel.fileIdFile.length>0){
        this.bizFormModel.fileIdFile.forEach(item=>{
      //  let userHeaderFile = {};
      let userHeaderFile = new FormData();
         userHeaderFile.append("entity", JSON.stringify(this.bizFormModel.inspectionCheckInfo));
        userHeaderFile.append("deleteIds",JSON.stringify(this.fileIds));
        userHeaderFile.append("fileIdUploads",item);
        //  userHeaderFile.entity=JSON.stringify(this.bizFormModel.inspectionCheckInfo);
        // userHeaderFile.deleteIds=JSON.stringify([this.photoId]);
        // userHeaderFile.fileIdUploads=item;
     
      saveInspectionCheckInfo(userHeaderFile).then((res)=>{
          if(res.code=="100"){
            this.$message.success("操作成功!")
            this.getInspectionCheckList()
          }else{
            this.$message.error("保存失败!")
          }
          this.dialogVisible=false
          this.resetLoad()
          this.flages=false
      }).catch((error)=>{
        this.$message.error("执行失败，请联系管理处理!")
        this.flages=false
      })
      })
     }else{
        let userHeaderFile = new FormData();
         userHeaderFile.append("entity", JSON.stringify(this.bizFormModel.inspectionCheckInfo));
        userHeaderFile.append("deleteIds",JSON.stringify(this.fileIds));
        userHeaderFile.append("fileIdUploads",this.bizFormModel.fileIdFile);
        //  userHeaderFile.entity=JSON.stringify(this.bizFormModel.inspectionCheckInfo);
        // userHeaderFile.deleteIds=JSON.stringify([this.photoId]);
        // userHeaderFile.fileIdUploads=item;
     
      saveInspectionCheckInfo(userHeaderFile).then((res)=>{
          if(res.code=="100"){
            this.$message.success("操作成功!")
            this.dialogVisible=false
            this.getInspectionCheckList()
          }
      }).catch((error)=>{
        this.$message.error("执行失败，请联系管理处理!")
      })
     }
    },
    handleRemove(file, fileList){
      this.bizFormModel.uploadFile=""
      this.bizFormModel.fileIdFile=""
      console.log(file,fileList,'hfsakfhaslkjf ');
    },
    handleExceed(file, fileList){
      // this.$message.warning(`当前限制选择 3 个文件，本次选择了 ${file.length} 个文件，共选择了 ${file.length + fileList.length} 个文件`);
      this.$message.warning(`当前只支持上传单个文件`);
    },
    hanldeError(err, file, fileList){
      console.log(err,'gsahalkxnnxcoao');
    },
    handlePreview(file){
      // 创建a标签
      const link = document.createElement('a');
      // download属性
      link.setAttribute('download', file.name);
      // href链接
      link.setAttribute('href', file.operationManual);
      // 自执行点击事件
      link.click();
    },
    // 上传文件
    handleLicensePreview(file,fileList) {
      const isLt2M = file.size / 1024 / 1024 < 20;
      if (!isLt2M) {
        this.$message.error(this.inter.sctpdx + " 20MB!");
      }else if(fileList.length>1){
        this.$message.error("暂只支持上传一个文件");
        return false;
      }
       else {
        // 存储文件，点击确认按钮时统一上传

        this.bizFormModel.uploadFile = URL.createObjectURL(file.raw);
        console.log(this.bizFormModel.uploadFile,"这安徽康师傅沙龙课拉开");
        this.bizFormModel.fileIdFile = file.raw;
        console.log(this.bizFormModel.fileIdFile,"这安徽康师傅沙龙课拉开");
      }
    },
   indexMethod(index){
       return (this.currentPage-1)*this.search.limit+index +1;
    },
    //上传限制
    beforeLicenseUpload(file) {
      const fileSuffix = file.name.substring(file.name.lastIndexOf(".") + 1);
       const whiteList = ["jpg", "jpeg", "png", "gif", "JPG", "JPEG","rar","zip","doc","docx","pdf", "xls", "xlsx", "ppt","txt"];

         console.log(fileSuffix,'这是什么卡号给凯撒好看');
      if (whiteList.indexOf(fileSuffix) === -1) {
        this.$message.error("文件格式不允许");
        return false;
      }
      const isLt2M = file.size / 1024 / 1024 < 20;
      if (!isLt2M) {
        this.$message.error(" 上传上传小于20MB的文件!");
        return false;
      }
     
    },

    fill(row,index,flag){
      this.fileIds=[]
      console.log(flag,'ajkfskja');
      this.inspectionCheckTableData=[]
      this.bizFormModel={
        fileIdFile:[],   //文件
        uploadFile:[],  //文件
        inspectionCheckInfo:{}
      }
      if(row.type=="0"){
        // 通过项目是否为打包项目
        getInspectionCheckInfoByInspecId(row.id).then((res)=>{
          if(res.code=="100"){
            this.bizFormModel.inspectionCheckInfo=res.data
            this.typeName="填写检验报告"
            this.type="0"
            this.inspectionIndex=index
             if(flag==1){
              this.flags=true
              this.switchEdit=1
            }else{
              this.flags=false
              this.switchEdit=0
            }
            
            let sex=row.sex=="gender_0"?"男":"女"
            this.bizFormModel.inspectionCheckInfo.name=row.patientName+"/"+sex+"/"+row.patient.age+"岁"
            this.getPhoto(this.bizFormModel.inspectionCheckInfo.fileId)

            this.photoId=this.bizFormModel.inspectionCheckInfo.fileId
            this.uploadFiles=this.bizFormModel.inspectionCheckInfo.id
            console.log(this.bizFormModel,'jiusfajhfak');
            this.dialogVisible=true
          }else{
            this.$message.error("后台数据异常，请重试或联系管理员!")
          }
        }).catch((error)=>{
          this.$message.error("afasfasf")
        })
      }else{
         // 通过项目是否为打包项目
        getInspectionCheckInfoByInspecId(row.id).then((res)=>{
          if(res.code=="100"){
            console.log(res.data,'ajkfskja');
            this.bizFormModel.inspectionCheckInfo=res.data
            this.typeName="填写检查报告"
            this.type="1"
            this.inspectionIndex=index
             if(flag==1){
              this.flags=true
              this.switchEdit=1
            }else{
              this.flags=false
              this.switchEdit=0
            }
            
            console.log(row,'ahglakglsak');
            let sex=row.patient.gender=="gender_0"?"男":"女"
            this.bizFormModel.inspectionCheckInfo.name=row.patientName+"/"+sex+"/"+row.patient.age+"岁"
            this.getPhoto(this.bizFormModel.inspectionCheckInfo.fileId)
            this.photoId=this.bizFormModel.inspectionCheckInfo.fileId
            this.uploadFiles=this.bizFormModel.inspectionCheckInfo.id
            console.log(this.bizFormModel,'jiusfajhfak');
            this.dialogVisible=true
          }else{
            this.$message.error("后台数据异常，请重试或联系管理员!")
          }
        }).catch((error)=>{
          this.$message.error("afasfasf")
        })
      }
      //this.dialogVisible=true
    },
    getInspectionCheckList() {
      this.setLoad()
      /* 查询参数 和数据权限 */
      this.search.params = [{columnName: 'company_id', queryType: '=', value: currentUser.company.id}]
      if(this.moreCodition) {
        this.search.params = this.search.params.concat(this.compositeCondition())
      }else{
        // 查询参数: 项目名称
        this.search.params.push({
      	  columnName: 'patient_name',
      	  queryType: 'like',
          value: this.queryModel.patientName
        })
        this.search.params.push({
          columnName: 'complete_by',
      	  queryType: 'like',
          value: this.queryModel.completeBy
        })
        this.search.params.push({
          columnName: 'status',
      	  queryType: '=',
          value: this.queryModel.status
        })
         if (this.queryModel.completeDate && this.queryModel.completeDate.length) {
                  this.search.params.push({
                      logic: "AND",
                      queryType: "("
                    },{
                      columnName: "complete_date",
                      logic: "",
                      queryType: 'between',
                      value: this.queryModel.completeDate,
                    },{
                      logic: "",
                      queryType: ")"
                    })
                }
      }
      // 数据权限: 检验检查inspection_check
      this.pushDataPermissions(this.search.params, this.$route.meta.routerId, this.tableId)
      listInspectionCheckPage(this.search).then(responseData => {
        if(responseData.code == 100) {
          console.log("asgksaahakjflafjlksj");
          this.inspectionCheckTotal = responseData.data.total
          this.inspectionCheckList = responseData.data.rows
          let doctorArr=[]
          this.doctorList=[]
          if(responseData.data.rows){
            this.inspectionCheckList.forEach(element => {
            doctorArr.push(element.completeBy)
          });
          doctorArr=[...new Set(doctorArr)]
          let doctorObject={
            name:'',
            value:''
          }
          doctorArr.forEach(element=>{
            doctorObject.name=element
            this.doctorList.push(doctorObject)
          })
          }
          this.resetLoad()
          console.log(this.doctorList,'看医生');
        } else {
          this.resetLoad()
          this.showMessage(responseData)
        }
        this.resetLoad()
      }).catch(error => {
        this.outputError(error)
      })
    },
    onSearch() {
      if(this.moreCodition) {
        this.search.offset = 0
        this.currentPage = 1
        this.getInspectionCheckList()
      } else {
        this.$refs['queryForm'].validate(valid => {
          if (valid) {
            this.search.offset = 0
            this.currentPage = 1
            this.getInspectionCheckList()
          } else {
            return false
          }
        })
      }
    },
    onSizeChange(val) {
      this.currentPage = 1
      this.search.limit = val;
      this.search.offset = (this.currentPage - 1) * val
      this.getInspectionCheckList()
    },
    onCurrentChange(val) {
      this.search.offset = (val - 1) * this.search.limit
      this.currentPage = val
      this.getInspectionCheckList()
    },
    async pageInit() {
      console.log(this.columnViews,'fanlkfnaslfkan');
      this.setLoad()
      try {
        this.initOptions(this.queryModel)
        this.search.params = [{columnName: 'company_id', queryType: '=', value: currentUser.company.id}]
        // 数据权限: 检验检查inspection_check
        this.pushDataPermissions(this.search.params, this.$route.meta.routerId, this.tableId)
        let [listInspectionCheckRespData, listPermissionRespData] = await Promise.all([
          listInspectionCheckPage(this.search),
          listResourcePermission(this.$route.meta.routerId)
        ])
        if(listInspectionCheckRespData.code == 100 && listPermissionRespData.code == 100) {
          this.inspectionCheckTotal = listInspectionCheckRespData.data.total
          this.inspectionCheckList = listInspectionCheckRespData.data.rows
           let doctorArr=[]
         this.doctorList=[]
         if(listInspectionCheckRespData.data.rows){
            this.inspectionCheckList.forEach(element => {
            doctorArr.push(element.completeBy)
          });
          doctorArr=[...new Set(doctorArr)]
          let doctorObject={
            name:'',
            value:''
          }
          doctorArr.forEach(element=>{
            doctorObject.name=element
            this.doctorList.push(doctorObject)
          })
         }
          console.log(this.doctorList,'看医生');
          this.permission.view = listPermissionRespData.data.find(item => {
            return item.permission === 'inspectionCheck:read'
          })
          this.permission.export = listPermissionRespData.data.find(item => {
            return item.permission === 'inspectionCheck:export'
          })
          this.permission.add = listPermissionRespData.data.find(item => {
            return item.permission === 'inspectionCheck:create'
          })
          this.permission.edit = listPermissionRespData.data.find(item => {
            return item.permission === 'inspectionCheck:update'
          })
          this.permission.remove = listPermissionRespData.data.find(item => {
            return item.permission === 'inspectionCheck:delete'
          })
        } else {
          this.showMessage(listPermissionRespData.code != 100 ? listPermissionRespData : listInspectionCheckRespData)
        }
        this.resetLoad()
      } catch(error) {
        this.outputError(error) 
      }
    },
    onViewInspectionCheck(index, row) {
      this.setLoad()
      getInspectionCheckById(row.id).then(responseData => {
        if(responseData.code == 100) {
          this.$refs.inspectionCheckForm.$emit('openViewInspectionCheckDialog', responseData.data)
        } else {
          this.showMessage(responseData)
        }
        this.resetLoad()
      }).catch(error => {
        this.outputError(error)
      })
    },
    onCreateInspectionCheck() {
      this.$refs.inspectionCheckForm.$emit('openAddInspectionCheckDialog')
    },
    onEditInspectionCheck(index, row) {
      this.setLoad()
      getInspectionCheckById(row.id).then(responseData => {
        if(responseData.code == 100) {
          this.$refs.inspectionCheckForm.$emit('openEditInspectionCheckDialog', responseData.data)
        }else{
          this.showMessage(responseData)
        }
        this.resetLoad()
      }).catch(error => {
        this.outputError(error)
      })
    },
    onCopyInspectionCheck(index, row) {
      this.setLoad()
      getInspectionCheckById(row.id).then(responseData => {
        if(responseData.code == 100) {
          this.$refs.inspectionCheckForm.$emit('openCopyInspectionCheckDialog', responseData.data)
        } else {
          this.showMessage(responseData)
        }
        this.resetLoad()
      }).catch(error => {
        this.outputError(error)
      })
    },
    onDeleteInspectionCheck(index, row) {
      this.$confirm('确定删除吗？', '确认', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.setLoad()
        deleteInspectionCheck(row).then(responseData => {
          if(responseData.code == 100) {
            this.getInspectionCheckList()
            this.showMessage({type: 'success', msg: '删除成功'})
          } else {
            this.showMessage(responseData)
          }
          this.resetLoad()
        }).catch(error => {
          this.outputError(error)  
        })
      }).catch(() => {})
    },
    onSortChange( orderby ) {
      if(validatenull(orderby.prop)) {
        this.search.columnName = ''
        this.search.order = ''
      } else  {
        this.search.columnName = orderby.prop
        this.search.order = orderby.order === 'descending' ? 'desc' : 'asc'
      }

      this.getInspectionCheckList()
    },
    initOptions(This) {
    } 
  },
  watch: {
     // tableData是el-table绑定的数据
      tableData: {
          // 解决表格显示错位问题
          handler () {
              this.$nextTick(() => {
                  // tableRef是el-table绑定的ref属性值
                  this.$refs.tableRef.doLayout()// 对 Table 进行重新布局
              })
          },
          deep: true
      }
  },
  updated(){
    if(this.$refs.tableRef){
       this.$nextTick(() => {
            // tableRef是el-table绑定的ref属性值
            this.$refs.tableRef.doLayout()// 对 Table 进行重新布局
        })
    }
  },
  mounted() {
    this.pageInit()
  }
}
</script>
<style lang="scss" scoped>
.avatar {
    width: 50%;
    height: 50%;
    display: block;
  }

  
</style>
<style lang="scss" scoped>
  .drag_table {
 // 设置表格header的高度
 /deep/ th {
   height: 44px;
 }
/deep/ th.gutter:last-of-type {
  height: 0 !important;
}
 // 设置表格body的高度
 /deep/.el-table__body-wrapper {
  //解决数据展示超出body高度不滚动bug
  overflow-y: auto;
   // 减去的是表格header的高度
   height: calc(100% - 44px) !important;
 }

 .el-table__fixed-right {
      height: 100% !important;
  }
}
</style>
<style scoped>
/deep/ .el-table__body-wrapper{
    height: calc(100% - 44px) !important;
  }
</style>