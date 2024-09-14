<template>
  <el-dialog :title='dialogProps.title' :visible.sync='dialogProps.visible' :close-on-click-modal='false' fullscreen :lock-scroll='true'
    @open='onDialogOpen()' v-loading='loading'>
    <div slot='title' class='dialog-header'>
      <el-row :span='24'>
        <el-col :span='10'>
          {{ dialogProps.title }}
          <OperationIcon v-show='dialogProps.action == "view" && permission.edit' type='primary' text='编辑' placement='top-start' icon-name='el-icon-edit' @click='switchEdit'></OperationIcon>
        </el-col>
        <el-col :span='14'>
          <el-tabs v-model='tabIndex'>
            <el-tab-pane label='基本信息' name='1'></el-tab-pane>
            <el-tab-pane label='约束条件' name='2'></el-tab-pane>
            <el-tab-pane label='存储配置' name='3'></el-tab-pane>
            <el-tab-pane label='Java配置' name='4'></el-tab-pane>
            <el-tab-pane label='列表配置' name='5'></el-tab-pane>
            <el-tab-pane label='编辑配置' name='6'></el-tab-pane>
          </el-tabs>
        </el-col>
      </el-row>
    </div>    
    
    <el-form :model='bizFormModel' :rules='formRules' 
      ref='genTableForm' label-width='120px' label-position='right' class='edit-form'>    
    <!-- 主表单  开始-->
      <div v-show='tabIndex=="1"'>
      <el-row>
        <el-col :span='24/2'>
          <el-form-item label='名称' prop='name'>
             <el-input v-if='dialogProps.action == "view" || dialogProps.action == "edit"' :disabled=true v-model='bizFormModel.name'></el-input>
             <el-select v-else v-model='bizFormModel' value-key='name' @change='onTableChange' filterable clearable placeholder='请选择名称'>
               <el-option v-for='st in st_List' :key='st.name' :label='st.name + " : " + st.comments' :value='st'></el-option>    
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span='24/2'>
          <el-form-item label='说明' prop='comments'>
            <el-input :disabled='dialogProps.action == "view"' v-model='bizFormModel.comments'  :maxlength='500'  :placeholder='dialogProps.action == "view"? "" : "请输入说明"'  ></el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span='24/2'>
          <el-form-item label='实体类名称' prop='className'>
            <el-input :disabled='dialogProps.action == "view"' v-model='bizFormModel.className'  :maxlength='100'  :placeholder='dialogProps.action == "view"? "" : "请输入实体类名称"'  ></el-input>
          </el-form-item>
        </el-col>
        <el-col :span='24/2'>
          <el-form-item label='关联父表' prop='parentTable.id'>
            <el-input v-if='dialogProps.action == "view"' :disabled=true v-model='bizFormModel.parentTable.name'></el-input>
            <el-select v-else v-model='bizFormModel.parentTable' value-key='id' filterable clearable placeholder='请选择关联父表' @clear='bizFormModel.parentTable={"id": null,"name": null}'>
               <el-option v-for='parentTable in parentTable_List' :key='parentTable.id' :label='parentTable.name + " : " + parentTable.comments' :value='parentTable'></el-option>    
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row v-show='bizFormModel.parentTable && bizFormModel.parentTable.name'>
        <el-col :span='24/2'>
          <el-form-item label='父表外键' prop='parentTableFk'>
            <el-input v-if='dialogProps.action == "view"' :disabled=true v-model='bizFormModel.parentTableFk'></el-input>
            <el-select v-else v-model='bizFormModel.parentTableFk' @change='onFkChange' value-key='name' filterable clearable placeholder='请选择父表外键'>
               <el-option v-for='parentTableFk in parentTableFk_List' :key='parentTableFk.name' :label='parentTableFk.name + " : " + parentTableFk.comments' :value='parentTableFk.name'></el-option>    
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span='24/2'>
          <el-form-item label='是关联表' prop='isAssociation'>
            <el-switch :disabled='dialogProps.action == "view"' v-model='bizFormModel.isAssociation' active-color='#13ce66' inactive-color='#dbdfe6' active-value='1' inactive-value='0'></el-switch>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span='24/2'>
          <el-form-item label='排序字段' prop='orderColumns'>
            <StringMultiSelect :disabled='dialogProps.action == "view"' v-model='bizFormModel.orderColumns' :opts='orderColumns_List' valKey='name' label='comments' placeholder='请选排序字段'></StringMultiSelect>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col>
          <el-form-item label='筛选条件' prop='filter'>        
            <el-input :disabled='dialogProps.action == "view"' v-model='bizFormModel.filter' type='textarea'  
              :maxlength='2000' :placeholder='dialogProps.action == "view"? "" : "请输入筛选条件"'  ></el-input>
          </el-form-item>
        </el-col>
      </el-row>
      </div>
               <!-- 主表单  结束-->
    
    <!--子表：   约束字段 开始-->
      <div v-show='tabIndex=="2"'>
      <el-row>
        <el-col>
    <el-table ref='genTable' :data='bizFormModel.constraintList' border @current-change='(currentRow, oldCurrentRow) => {constraintCurrentRow = currentRow}'>
      <el-table-column prop='name' label='名称' min-width='180px' header-align='center'>
        <template slot='header' slot-scope='{row,$index}'><span style="color:#F56C6C;margin-right: 4px">*</span>名称</template>
        <template slot-scope='{row,$index}'>
          <el-form-item v-if='dialogProps.action != "view" && row === constraintCurrentRow' :prop="`constraintList.${$index}.name`" :rules='formRules.constraint_name' label-width=0>
            <el-input v-model='row.name'  :maxlength='200' clearable placeholder='请输入名称'></el-input>
          </el-form-item>
          <span v-else>{{row.name}}</span>
        </template>
      </el-table-column>
      <el-table-column prop='comments' label='描述' min-width='200px' header-align='center'>
        <template slot='header' slot-scope='{row,$index}'>描述</template>
        <template slot-scope='{row,$index}'>
          <el-form-item v-if='dialogProps.action != "view" && row === constraintCurrentRow' :prop="`constraintList.${$index}.comments`" :rules='formRules.constraint_comments' label-width=0>
            <el-input v-model='row.comments'  :maxlength='500' clearable placeholder='请输入描述'></el-input>
          </el-form-item>
          <span v-else>{{row.comments}}</span>
        </template>
      </el-table-column>
      <el-table-column prop='constraintColumn' label='约束字段' min-width='180px' header-align='center'>
        <template slot='header' slot-scope='{row,$index}'>约束字段</template>
        <template slot-scope='{row,$index}'>
          <el-form-item v-if='dialogProps.action != "view" && row === constraintCurrentRow' :prop="`constraintList.${$index}.constraintColumn`" :rules='formRules.constraint_constraintColumn' label-width=0>
            <StringMultiSelect v-model='row.constraintColumn' :opts='constraintColumn_Constraint_List' valKey='name' label='name' placeholder='请选择约束字段'></StringMultiSelect>
          </el-form-item>
          <span v-else>{{row.constraintColumn}}</span>
        </template>
      </el-table-column>
      <el-table-column v-if='dialogProps.action != "view"' label='操作' header-align='center' align='center' width='60px' fixed='right'>
        <template slot-scope='scope'>
          <el-tooltip class='item' effect='light' content='删除' placement='top-start'>
            <i class='el-icon-delete' style='color:#F56C6C;cursor:pointer;'
              @click='onDeleteRow(scope.$index, bizFormModel.constraintList)'></i>
          </el-tooltip>         
        </template>
      </el-table-column>          
    </el-table>
    <el-button v-if='dialogProps.action != "view"' type='primary' 
      @click='onAddConstraintRow(bizFormModel.constraintList)'>添加</el-button>
        </el-col>        
      </el-row>
      </div>
      <!--子表：   约束字段 结束-->
    <!--子表：   业务表字段 开始-->
      <div v-show='tabIndex>"2"'>
      <el-row>
        <el-col>
    <el-table ref='genTable' :data='bizFormModel.genTableColumnList' border @current-change='(currentRow, oldCurrentRow) => {genTableColumnCurrentRow = currentRow}'>
      <el-table-column prop='name' label='字段名' min-width='180px' header-align='center' fixed></el-table-column>
        <el-table-column v-if='tabIndex=="3"' key='comments' prop='comments' label='说明' min-width='200px' header-align='center'>
        <template slot='header' slot-scope='{row,$index}'>说明</template>
        <template slot-scope='{row,$index}'>
          <el-form-item v-if='dialogProps.action != "view" && row === genTableColumnCurrentRow' :prop="`genTableColumnList.${$index}.comments`" :rules='formRules.genTableColumn_comments' label-width=0>
            <el-input v-model='row.comments'  :maxlength='500' clearable placeholder='请输入说明'></el-input>
          </el-form-item>
          <span v-else>{{row.comments}}</span>
        </template>
      </el-table-column>
      <el-table-column v-if='tabIndex=="3"' key='jdbcType' prop='jdbcType' label='物理类型' min-width='160px' header-align='center'></el-table-column>
      <el-table-column v-if='tabIndex=="4"' key='javaType.value' prop='javaType.value' label='JAVA类型' min-width='100px' header-align='center'>
        <template slot='header' slot-scope='{row,$index}'><span style="color:#F56C6C;margin-right: 4px">*</span>JAVA类型</template>
        <template slot-scope='{row,$index}'>
          <el-form-item v-if='dialogProps.action != "view" && row === genTableColumnCurrentRow' :prop="`genTableColumnList.${$index}.javaType`" :rules='formRules.genTableColumn_javaType' label-width=0>
            <el-select v-model='row.javaType' value-key='value' @change='onJavaTypeChange' filterable clearable placeholder='请选择JAVA类型' @focus='setCurrentFocus(row)'>
              <el-option v-for='javaType in javaType_GenTableColumn_List' :key='javaType.value' :label='javaType.name' :value='javaType'></el-option>    
            </el-select>
          </el-form-item>
          <span v-else>{{row.javaType.name}}</span> 
        </template>
      </el-table-column>
        <el-table-column v-if='tabIndex=="4"' key='customType.id' prop='customType.id' label='自定义类型' min-width='180px' header-align='center'>
        <template slot='header' slot-scope='{row,$index}'>自定义类型</template>
        <template slot-scope='{row,$index}'>
          <el-form-item v-if='dialogProps.action != "view" && row === genTableColumnCurrentRow' :prop="`genTableColumnList.${$index}.customType`" :rules='formRules.genTableColumn_customType' label-width=0>
            <el-select v-model='row.customType' value-key='id' filterable clearable placeholder='请选择自定义类型' @focus='setCurrentFocus(row)'>
              <el-option v-for='customType in customType_GenTableColumn_List' :key='customType.id' :label='customType.name' :value='customType'></el-option>    
            </el-select>
          </el-form-item>
          <span v-else>{{row.customType.name}}</span> 
        </template>
      </el-table-column>
        <el-table-column v-if='tabIndex=="4"' key='javaField' prop='javaField' label='属性名' min-width='180px' header-align='center'>
        <template slot='header' slot-scope='{row,$index}'><span style="color:#F56C6C;margin-right: 4px">*</span>属性名</template>
        <template slot-scope='{row,$index}'>
          <el-form-item v-if='dialogProps.action != "view" && row === genTableColumnCurrentRow' :prop="`genTableColumnList.${$index}.javaField`" :rules='formRules.genTableColumn_javaField' label-width=0>
            <el-input v-model='row.javaField'  :maxlength='1024' clearable placeholder='请输入属性名'></el-input>
          </el-form-item>
          <span v-else>{{row.javaField}}</span>
        </template>
      </el-table-column>
        <el-table-column v-if='tabIndex=="4"' key='defVal' prop='defVal' label='默认值' min-width='160px' header-align='center'>
        <template slot='header' slot-scope='{row,$index}'>默认值</template>
        <template slot-scope='{row,$index}'>
          <el-form-item v-if='dialogProps.action != "view" && row === genTableColumnCurrentRow' :prop="`genTableColumnList.${$index}.defVal`" :rules='formRules.genTableColumn_defVal' label-width=0>
            <el-input v-model='row.defVal'  :maxlength='2000' clearable placeholder='请输入默认值'></el-input>
          </el-form-item>
          <span v-else>{{row.defVal}}</span>
        </template>
      </el-table-column>
        <el-table-column v-if='tabIndex=="3"' key='isPk' prop='isPk' label='主键' min-width='60px' header-align='center' align='center'>
        <template slot='header' slot-scope='{row,$index}'>主键</template>
        <template slot-scope='{row,$index}'>
          <el-form-item v-if='dialogProps.action != "view"' :prop="`genTableColumnList.${$index}.isPk`" :rules='formRules.genTableColumn_isPk' label-width=0>
            <el-switch v-model='row.isPk' active-color='#13ce66' inactive-color='#dbdfe6' active-value='1' inactive-value='0'></el-switch>
          </el-form-item>
            <li v-if='dialogProps.action == "view" && row.isPk == "1"' class='el-icon-check' style='color:#F56C6C;'></li>
        </template>
      </el-table-column>
        <el-table-column v-if='tabIndex=="3"' key='isNull'  prop='isNull' label='可空' min-width='60px' header-align='center' align='center'>
        <template slot='header' slot-scope='{row,$index}'>可空</template>
        <template slot-scope='{row,$index}'>
          <el-form-item v-if='dialogProps.action != "view"' :prop="`genTableColumnList.${$index}.isNull`" :rules='formRules.genTableColumn_isNull' label-width=0>
            <el-switch v-model='row.isNull' active-color='#13ce66' inactive-color='#dbdfe6' active-value='1' inactive-value='0'></el-switch>
          </el-form-item>
            <li v-if='dialogProps.action == "view" && row.isNull == "1"' class='el-icon-check' style='color:#F56C6C;'></li>
        </template>
      </el-table-column>
        <el-table-column v-if='tabIndex=="3"' key='isInsert' prop='isInsert' label='可插' min-width='60px' header-align='center' align='center'>
        <template slot='header' slot-scope='{row,$index}'>可插</template>
        <template slot-scope='{row,$index}'>
          <el-form-item v-if='dialogProps.action != "view"' :prop="`genTableColumnList.${$index}.isInsert`" :rules='formRules.genTableColumn_isInsert' label-width=0>
            <el-switch v-model='row.isInsert' active-color='#13ce66' inactive-color='#dbdfe6' active-value='1' inactive-value='0'></el-switch>
          </el-form-item>
            <li v-if='dialogProps.action == "view" && row.isInsert == "1"' class='el-icon-check' style='color:#F56C6C;'></li>
        </template>
      </el-table-column>
        <el-table-column v-if='tabIndex=="6"' key='isEdit' prop='isEdit' label='可编' min-width='60px' header-align='center' align='center'>
        <template slot='header' slot-scope='{row,$index}'>可编</template>
        <template slot-scope='{row,$index}'>
          <el-form-item v-if='dialogProps.action != "view"' :prop="`genTableColumnList.${$index}.isEdit`" :rules='formRules.genTableColumn_isEdit' label-width=0>
            <el-switch v-model='row.isEdit' active-color='#13ce66' inactive-color='#dbdfe6' active-value='1' inactive-value='0'></el-switch>
          </el-form-item>
            <li v-if='dialogProps.action == "view" && row.isEdit == "1"' class='el-icon-check' style='color:#F56C6C;'></li>
        </template>
      </el-table-column>
        <el-table-column v-if='tabIndex=="5"' key='isList' prop='isList' label='列显' min-width='60px' header-align='center' align='center'>
        <template slot='header' slot-scope='{row,$index}'>列显</template>
        <template slot-scope='{row,$index}'>
          <el-form-item v-if='dialogProps.action != "view"' :prop="`genTableColumnList.${$index}.isList`" :rules='formRules.genTableColumn_isList' label-width=0>
            <el-switch v-model='row.isList' active-color='#13ce66' inactive-color='#dbdfe6' active-value='1' inactive-value='0'></el-switch>
          </el-form-item>
            <li v-if='dialogProps.action == "view" && row.isList == "1"' class='el-icon-check' style='color:#F56C6C;'></li>
        </template>
      </el-table-column>
        <el-table-column v-if='tabIndex=="5"' key='width' prop='width' label='列宽' min-width='80px' header-align='center' align='right'>
        <template slot='header' slot-scope='{row,$index}'><span style="color:#F56C6C;margin-right: 4px">*</span>列宽</template>
        <template slot-scope='{row,$index}'>
          <el-form-item v-if='dialogProps.action != "view" && row === genTableColumnCurrentRow' :prop="`genTableColumnList.${$index}.width`" :rules='formRules.genTableColumn_width' label-width=0>
            <el-input-number v-model='row.width' controls-position='right' :controls='false' style='width:100%;' placeholder='请输入列宽'></el-input-number>
          </el-form-item>
          <span v-else>{{row.width}}</span>
        </template>
      </el-table-column>
        <el-table-column v-if='tabIndex=="5"' key='isQuery' prop='isQuery' label='查询' min-width='60px' header-align='center' align='center'>
        <template slot='header' slot-scope='{row,$index}'>查询</template>
        <template slot-scope='{row,$index}'>
          <el-form-item v-if='dialogProps.action != "view"' :prop="`genTableColumnList.${$index}.isQuery`" :rules='formRules.genTableColumn_isQuery' label-width=0>
            <el-switch v-model='row.isQuery' active-color='#13ce66' inactive-color='#dbdfe6' active-value='1' inactive-value='0'></el-switch>
          </el-form-item>
            <li v-if='dialogProps.action == "view" && row.isQuery == "1"' class='el-icon-check' style='color:#F56C6C;'></li>
        </template>
      </el-table-column>
        <el-table-column v-if='tabIndex=="5"' key='queryType.value' prop='queryType.value' label='查询方式' min-width='120px' header-align='center'>
        <template slot='header' slot-scope='{row,$index}'>查询方式</template>
        <template slot-scope='{row,$index}'>
          <el-form-item v-if='dialogProps.action != "view" && row === genTableColumnCurrentRow' :prop="`genTableColumnList.${$index}.queryType`" :rules='formRules.genTableColumn_queryType' label-width=0>
            <el-select v-model='row.queryType' value-key='value' filterable clearable placeholder='请选择查询方式' @focus='setCurrentFocus(row)'>
              <el-option v-for='queryType in queryType_GenTableColumn_List' :key='queryType.value' :label='queryType.name' :value='queryType'></el-option>    
            </el-select>
          </el-form-item>
          <span v-else>{{row.queryType.name}}</span> 
        </template>
      </el-table-column>
        <el-table-column v-if='tabIndex=="5"' key='queryVal' prop='queryVal' label='查询条件' min-width='180px' header-align='center'>
        <template slot='header' slot-scope='{row,$index}'>查询条件</template>
        <template slot-scope='{row,$index}'>
          <el-form-item v-if='dialogProps.action != "view" && row === genTableColumnCurrentRow' :prop="`genTableColumnList.${$index}.queryVal`" :rules='formRules.genTableColumn_queryVal' label-width=0>
            <el-input v-model='row.queryVal'  :maxlength='2000' clearable placeholder='请输入查询条件'></el-input>
          </el-form-item>
          <span v-else>{{row.queryVal}}</span>
        </template>
      </el-table-column>
        <el-table-column v-if='tabIndex=="6"' key='showType.value' prop='showType.value' label='控件' min-width='180px' header-align='center'>
        <template slot='header' slot-scope='{row,$index}'>控件</template>
        <template slot-scope='{row,$index}'>
          <el-form-item v-if='dialogProps.action != "view" && row === genTableColumnCurrentRow' :prop="`genTableColumnList.${$index}.showType`" :rules='formRules.genTableColumn_showType' label-width=0>
            <el-select v-model='row.showType' value-key='value' filterable clearable placeholder='请选择控件' @focus='setCurrentFocus(row)'>
              <el-option v-for='showType in showType_GenTableColumn_List' :key='showType.value' :label='showType.name' :value='showType'></el-option>    
            </el-select>
          </el-form-item>
          <span v-else>{{row.showType.name}}</span> 
        </template>
      </el-table-column>
        <el-table-column v-if='tabIndex=="6"' key='interaction' prop='interaction' label='联动变量' min-width='200px' header-align='center'>
        <template slot='header' slot-scope='{row,$index}'>联动变量</template>
        <template slot-scope='{row,$index}'>
          <el-form-item v-if='dialogProps.action != "view" && row === genTableColumnCurrentRow' :prop="`genTableColumnList.${$index}.interaction`" :rules='formRules.genTableColumn_interaction' label-width=0>
            <StringMultiSelect v-model='row.interaction' :opts='interaction_GenTableColumn_List' valKey='name' label='comments' placeholder='请选择联动变量'></StringMultiSelect>
          </el-form-item>
          <span v-else>{{row.interaction}}</span>
        </template>
      </el-table-column>
        <el-table-column v-if='tabIndex=="6"' key='interactionVal' prop='interactionVal' label='联动值' min-width='200px' header-align='center'>
        <template slot='header' slot-scope='{row,$index}'>联动值</template>
        <template slot-scope='{row,$index}'>
          <el-form-item v-if='dialogProps.action != "view" && row === genTableColumnCurrentRow' :prop="`genTableColumnList.${$index}.interactionVal`" :rules='formRules.genTableColumn_interactionVal' label-width=0>
            <el-input v-model='row.interactionVal'  :maxlength='2000' clearable placeholder='请输入联动值'></el-input>
          </el-form-item>
          <span v-else>{{row.interactionVal}}</span>
        </template>
      </el-table-column>      
        <el-table-column v-if='tabIndex=="6"' key='parameters' prop='parameters' label='联动参数' min-width='200px' header-align='center'>
        <template slot='header' slot-scope='{row,$index}'>联动参数</template>
        <template slot-scope='{row,$index}'>
          <el-form-item v-if='dialogProps.action != "view" && row === genTableColumnCurrentRow' :prop="`genTableColumnList.${$index}.parameters`" :rules='formRules.genTableColumn_parameters' label-width=0>
            <el-input v-model='row.parameters'  :maxlength='2000' clearable placeholder='请输入联动参数'></el-input>
          </el-form-item>
          <span v-else>{{row.parameters}}</span>
        </template>
      </el-table-column>
        <el-table-column v-if='tabIndex=="5"' key='sort' prop='sort' label='排序' min-width='80px' header-align='center' align='right'>
        <template slot='header' slot-scope='{row,$index}'>排序</template>
        <template slot-scope='{row,$index}'>
          <el-form-item v-if='dialogProps.action != "view" && row === genTableColumnCurrentRow' :prop="`genTableColumnList.${$index}.sort`" :rules='formRules.genTableColumn_sort' label-width=0>
            <el-input-number v-model='row.sort' controls-position='right' :controls='false' style='width:100%;' placeholder='请输入排序'></el-input-number>
          </el-form-item>
          <span v-else>{{row.sort}}</span>
        </template>
      </el-table-column>
      <el-table-column v-if='dialogProps.action != "view"' label='操作' header-align='center' align='center' width='60px' fixed='right'>
        <template slot-scope='scope'>
          <el-tooltip class='item' effect='light' content='删除' placement='top-start'>
            <i class='el-icon-delete' style='color:#F56C6C;cursor:pointer;'
              @click='onDeleteRow(scope.$index, bizFormModel.genTableColumnList)'></i>
          </el-tooltip>         
        </template>
      </el-table-column>          
    </el-table>
    <el-button v-if='dialogProps.action != "view"' type='primary' 
      @click='onAddGenTableColumnRow(bizFormModel.genTableColumnList)'>添加</el-button>
      <el-button v-if='dialogProps.action != "view"' :plain='true' @click='onRefreshColumn()'>刷新</el-button>      
        </el-col>        
      </el-row>
      </div>
      <!--子表：   业务表字段 结束-->
    </el-form>
        
    <!-- 按钮  开始-->
    <span slot='footer' class='dialog-footer'>
      <el-button v-if='dialogProps.action != "view"' :disabled="flage" type='primary'  @click='onSubmit("genTableForm")'>保 存</el-button>    
      <el-button v-if='dialogProps.action != "view"' :plain='true' @click='onDialogClose()'>取 消</el-button>
      <el-button v-if='dialogProps.action == "view"' :plain='true' @click='onDialogClose()'>关 闭</el-button>
    </span> 
    <!-- 按钮 结束-->
  </el-dialog>
</template>

<script>
import { listSchemaColumnAll } from '@/api/gen/schemaColumn'  
import { listSchemaTableAll } from '@/api/gen/schemaTable'
import { listSchemaConstraintAll } from '@/api/gen/schemaConstraint'
import { validatenull } from '@/utils/validate'
import StringMultiSelect from '@/components/StringMultiSelect'

import { listGenTableAll } from '@/api/gen/genTable'
import { listGenTableColumnAll } from '@/api/gen/genTableColumn'
import { listDictItemAll } from '@/api/sys/dictItem'
import { listGenSchemeAll } from '@/api/gen/genScheme'

import { saveGenTable } from '@/api/gen/genTable'
import BaseUI from '@/views/components/baseUI'
import OperationIcon from '@/components/OperationIcon'
export default {
  extends: BaseUI,
  name: 'genTable-form',
  components: {
    StringMultiSelect,
    OperationIcon
  },  
  data() {
    return {
      tabIndex: '1',
      bizFormModel: this.initFormModel(),
          st_List: [],  // 数据库表列表
          parentTable_List: [],  // 关联父表
          parentTableFk_List: [],  // 父表外键
    orderColumns_List: [],        // 排序字段列表
    constraintColumn_Constraint_List: [],    // 约束字段
    javaType_GenTableColumn_List: [],    // JAVA类型
    customType_GenTableColumn_List: [],    // 自定义类型
    queryType_GenTableColumn_List: [],    // 查询方式
    showType_GenTableColumn_List: [],    // 控件
    interaction_GenTableColumn_List: [],    // 联动变量
    flage:false,
       dialogProps: {
        visible: false,
        action: '',
        title: '',
        
      },
      formRules: {
        // 主表验证
        'name': [
            { required: true, message: '请输入名称', trigger: 'blur' }
        ],
        'className': [
            { required: true, message: '请输入实体类名称', trigger: 'blur' }
        ],
        // 子表验证 约束字段
        'constraint_name': [
            { required: true, message: '请输入名称', trigger: 'blur' }
        ],
        // 子表验证 业务表字段
        'genTableColumn_name': [
            { required: true, message: '请输入字段名', trigger: 'blur' }
        ],
        'genTableColumn_jdbcType': [
            { required: true, message: '请输入物理类型', trigger: 'blur' }
        ],
        'genTableColumn_javaType': [
            { required: true, message: '请输入JAVA类型', trigger: 'blur' }
        ],
        'genTableColumn_javaField': [
            { required: true, message: '请输入属性名', trigger: 'blur' }
        ],
        'genTableColumn_width': [
            { required: true, message: '请输入列宽', trigger: 'blur' }
        ],
      },
    constraintCurrentRow: {},     // 子表 约束字段 当前选择行
    genTableColumnCurrentRow: {},     // 子表 业务表字段 当前选择行
    }    
  },
  props: {
    // 权限
    permission: {
      type: Object
    }
  },    
  methods: {
    setCurrentFocus(row) {
      if (this.$refs.genTable) {
        this.$refs.genTable.setCurrentRow(row)
      }
    },
    onSubmit(formName) {
      this.flage=true
      this.$refs[formName].validate(valid => {
        if (valid) {
          this.doSave()
        } else {
          this.flage=false
          return false
        }
      });
    },
    doSave() {
      this.setLoad()
      saveGenTable(this.bizFormModel).then(responseData => {
        this.flage=false
        if(responseData.code == 100) {
          this.dialogProps.visible = false
          this.$emit('save-finished')
        } else {
          this.showMessage(responseData)
        }
        this.resetLoad()
      }).catch(error => {
        this.flage=false
        this.outputError(error)         
      })
    },
    onAddConstraintRow(tableData) {
        tableData.push({
            'genTable': {     // 父表 归属表编号
              'id': validatenull(parent) || validatenull(parent.genTable) ? null : parent.genTable.id,
              'name': validatenull(parent) || validatenull(parent.genTable) ? null : parent.genTable.name,
            },
            'name': '',   // 名称
            'comments': '',   // 描述
            'constraintColumn': '',   // 约束字段
            'remarks': '',   // 备注信息
        })
    },
    onAddGenTableColumnRow(tableData) {
        tableData.push({
            'genTable': {     // 父表 表编号
              'id': validatenull(parent) || validatenull(parent.genTable) ? null : parent.genTable.id,
              'name': validatenull(parent) || validatenull(parent.genTable) ? null : parent.genTable.name,
            },
            'name': '',   // 字段名
            'comments': '',   // 说明
            'jdbcType': '',   // 物理类型
            'javaType': {     // JAVA类型
              'value': '',
              'name': '',
            },
            'customType': {     // 自定义类型
              'id': null,
              'name': '',
            },
            'javaField': '',   // 属性名
            'defVal': '',   // 默认值
            'isPk': '',   // 主键
            'isNull': '',   // 可空
            'isInsert': '',   // 可插
            'isEdit': '',   // 可编
            'isList': '',   // 列显
            'width': 0,    // 列宽
            'isQuery': '',   // 查询
            'queryType': {     // 查询方式
              'value': '',
              'name': '',
            },
            'queryVal': '',   // 查询条件
            'showType': {     // 控件
              'value': '',
              'name': '',
            },
            'interaction': '',   // 联动变量
            'parameters': '',   // 联动参数
            'settings': '',   // 其它设置（扩展字段JSON）
            'sort': 0,    // 排序
            'remarks': '',   // 备注信息
        })
    },
    onDeleteRow(index, tableData) {
        tableData.splice(index, 1)
    },
    onDialogClose() {
      this.dialogProps.visible = false  
    },
    onDialogOpen() {
      this.$nextTick(() => {
        this.$refs['genTableForm'].clearValidate()
      })
    },
    switchEdit() {
      this.dialogProps.action = 'edit'
      this.dialogProps.title = '修改业务表'
      this.initOptions(this.bizFormModel)
    },
    initFormModel(parent) {
      return {
        'name': '',   // 名称        	
        'comments': '',   // 说明        	
        'className': '',   // 实体类名称        	
        'parentTable': {     // 关联父表
          'id': null,
          'name': null,
        },
        'parentTableFk': '',   // 父表外键
        'isAssociation': '',   // 是关联表        	
        'filter': '',   // 筛选条件
        'orderColumns': '',     	
        'remarks': '',   // 备注信息        	

        constraintList: [],       // 子表列表
        genTableColumnList: [],       // 子表列表
      }
    },
    initOptions() {
      // 主表
      let This = this.bizFormModel
      let st_search = {
        params: []
      }
      this.st_List = []
      listSchemaTableAll(st_search).then(responseData => {
        this.st_List = responseData.data
      })  
      let parentTable_search = {
        params: []
      }
      this.parentTable_List = []
      listGenTableAll(parentTable_search).then(responseData => {
        this.parentTable_List = responseData.data
      })
      //和列表共用  
      if(!validatenull(This) && !validatenull(This.genTableColumnList)){
        this.parentTableFk_List = This.genTableColumnList
        this.orderColumns_List = this.genOderColumns(This.genTableColumnList)
        this.constraintColumn_Constraint_List = This.genTableColumnList
	      this.interaction_GenTableColumn_List = This.genTableColumnList
      }


      // 子表  业务表字段
      This = this.genTableColumnCurrentRow
      let javaType_search = {
        params: [{columnName: 'dict_type_id', queryType: '=', value: '5001'}]
      }
      this.javaType_GenTableColumn_List = []
      listDictItemAll(javaType_search).then(responseData => {
        this.javaType_GenTableColumn_List = responseData.data
      })
      let customType_search = {
        params: []
      }
      this.customType_GenTableColumn_List = []
      listGenSchemeAll(customType_search).then(responseData => {
        this.customType_GenTableColumn_List = responseData.data
      })
      let queryType_search = {
        params: [{columnName: 'dict_type_id', queryType: '=', value: '5002'}]
      }
      this.queryType_GenTableColumn_List = []
      listDictItemAll(queryType_search).then(responseData => {
        this.queryType_GenTableColumn_List = responseData.data
      })
      let showType_search = {
        params: [{columnName: 'dict_type_id', queryType: '=', value: '5003'}]
      }
      this.showType_GenTableColumn_List = []
      listDictItemAll(showType_search).then(responseData => {
        this.showType_GenTableColumn_List = responseData.data
      })
      let interaction_search = {
        params: []
      }    
    },
    setRowClassName({row, rowIndex}) {
      row.sort = (rowIndex + 1) * 10
    },
    async onTableChange(newValue) {
      if(validatenull(newValue)){
        this.parentTableFk_List = []
        this.orderColumns_List = []
        this.constraintColumn_Constraint_List = []
        this.interaction_GenTableColumn_List = []
        this.bizFormModel = this.initFormModel()
      }else{
        this.loading = true
        // 初始化类名
        this.bizFormModel.className = this.bizFormModel.name.substring(0, 1).toUpperCase() +  this.bizFormModel.name.substring(1).replace(/\_(\w)/g, function(all, letter){
          return letter.toUpperCase()
        })
        try {
          let search = {
            params: [{
              columnName: 'table_name',
              queryType: '=',
              value: newValue.name
            }]
          }
          let [columnRespData, constraintRespData] = await Promise.all([
            listSchemaColumnAll(search),
            listSchemaConstraintAll(newValue.name)
          ])
          if(columnRespData.code == 100 && constraintRespData.code == 100) {
            this.bizFormModel.isAssociation = '0'
            this.bizFormModel.genTableColumnList = columnRespData.data
            this.parentTableFk_List = this.bizFormModel.genTableColumnList
            this.orderColumns_List = this.genOderColumns(this.bizFormModel.genTableColumnList)
            this.constraintColumn_Constraint_List = this.bizFormModel.genTableColumnList
            this.interaction_GenTableColumn_List = this.bizFormModel.genTableColumnList
            this.bizFormModel.constraintList = constraintRespData.data
          } else {
            this.showMessage(columnRespData.code != 100 ? columnRespData : constraintRespData)
          }
          this.loading = false           
        } catch(error) {
          this.outputError(error)        
        }
      }
    },
    onFkChange(newValue) {
      if(newValue && newValue != 'id') {
        this.bizFormModel.genTableColumnList.forEach(function(item, index, array){
          if(item.name == newValue && item.javaType.value != 'Custom') {
            item.javaType = {value: 'Custom', name:'Custom'}
            item.javaField = item.simpleJavaField + '.id|name'
            item.showType = {value: 'Select', name: '下拉列表选择器'}
          }
        })
      }
    },
    onJavaTypeChange(javaType) {
      if(javaType && (javaType.value == 'This' || javaType.value == 'Custom')) {
        if(this.genTableColumnCurrentRow.simpleJavaField.substring(this.genTableColumnCurrentRow.simpleJavaField.length - 2, this.genTableColumnCurrentRow.simpleJavaField.length) == 'Id') {
          this.genTableColumnCurrentRow.javaField = this.genTableColumnCurrentRow.simpleJavaField.substring(0, this.genTableColumnCurrentRow.simpleJavaField.length - 2) + '.id|name'
        } else {
          this.genTableColumnCurrentRow.javaField = this.genTableColumnCurrentRow.simpleJavaField + '.id|name'
        }
        this.genTableColumnCurrentRow.showType = {value: 'Select', name: '下拉列表选择器'}
      }else {
        this.genTableColumnCurrentRow.javaField = this.genTableColumnCurrentRow.simpleJavaField
        this.genTableColumnCurrentRow.showType = {value: 'SingleInput', name: '单行文本框'}
      }
    },
    onRefreshColumn() {
      if(!validatenull(this.bizFormModel.name)) {
        this.loading = true
        let search = {
          params: [{
            columnName: 'table_name',
            queryType: '=',
            value: this.bizFormModel.name
          }]
        }
        listSchemaColumnAll(search).then(responseData => {
          if(responseData.code == 100) {
            responseData.data.forEach((item)=>{
              let noFound = true
              for(let i = 0; i < this.bizFormModel.genTableColumnList.length; i++) {
                let it = this.bizFormModel.genTableColumnList[i]
                if(item.name == it.name) {
                  it.jdbcType = item.jdbcType
                  noFound = false
                  break
                }
              }
              if(noFound == true) {
                this.bizFormModel.genTableColumnList.push(item)
              }
            })
            this.loading = false
          } else {
            this.showMessage(responseData)
          }
        })
      }
    },
    genOderColumns(columns) {
      let list = []
      columns.forEach((col) => {
        list.push({name: 'a.' + col.name + ' ASC', comments: col.comments + ' 升序'})
        list.push({name: 'a.' + col.name + ' DESC', comments: col.comments + ' 降序'})
      })
      return list
    }
  },
  watch: {
  },
  mounted: function() {
    this.$nextTick(() => {
      this.$on('openViewGenTableDialog', function(genTable) {
        this.dialogProps.action = 'view'
        this.dialogProps.title = '查看业务表'
        this.tabIndex = "1"
        this.bizFormModel = genTable
        // this.initOptions()
        this.dialogProps.visible = true
      })    
      this.$on('openEditGenTableDialog', function(genTable) {
        this.dialogProps.action = 'edit'
        this.dialogProps.title = '修改业务表'
        this.tabIndex = "1"
        this.bizFormModel = genTable
        this.initOptions()
        this.dialogProps.visible = true
      })
      this.$on('openAddGenTableDialog', function() {
        this.dialogProps.action = 'add'
        this.dialogProps.title = '添加业务表'
        this.tabIndex = "1"
        this.bizFormModel = this.initFormModel()
        this.initOptions()
        this.dialogProps.visible = true
      })
      this.$on('openCopyGenTableDialog', function(genTable) {
        this.dialogProps.action = 'edit'
        this.dialogProps.title = '添加业务表'
        this.tabIndex = "1"
        this.bizFormModel = genTable
        this.initOptions()
        //把id设置为空，添加一个新的
        this.bizFormModel.id = null
        for (var i = 0; i <= this.bizFormModel.constraintList.length - 1; i++) {
            this.bizFormModel.constraintList[i].id = null
        }        
        for (var i = 0; i <= this.bizFormModel.genTableColumnList.length - 1; i++) {
            this.bizFormModel.genTableColumnList[i].id = null
        }        
        this.dialogProps.visible = true
      })      
    })
  }  
}
</script>