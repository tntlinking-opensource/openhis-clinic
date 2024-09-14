<template>
  <div class="condition-panel" v-loading='loading'>
    <el-row :key='"ConditionPanel" + idx' v-for='idx in [0, 1, 2]' :gutter='5'>
      <el-col :span='11'>
        <el-col :span='3'>
          <span v-if='idx == 0' style='text-align: center;display:block;'>一组</span>
          <el-select v-else v-model='groupOne[idx].logic' placeholder='请选择逻辑符'>
              <el-option v-for='item in logics' :key='item.value' :label='item.label' :value='item.value'></el-option>
          </el-select>
        </el-col>
        <el-col :span='5'>
          <el-select v-model='groupOne[idx].column' value-key='name' @change='onColumnChange(groupOne[idx].column, groupOne, idx)' placeholder='请选择字段'>
              <el-option v-for="col in columns.filter(item => item['name'] != exclude)" :key='col.name' :label='col.comments' :value='col'></el-option>
          </el-select>
        </el-col>
        <el-col :span='4'>
          <el-select v-model='groupOne[idx].queryType' @change='onQueryTypeChange(groupOne[idx].queryType, groupOne, idx)' placeholder='请选择操作符'>
            <el-option v-for='opr in groupOne[idx].operations' :key='opr.value' :label='opr.label' :value='opr.value'></el-option>
          </el-select>
        </el-col>
        <el-col :span='12'>
          <div v-if="groupOne[idx].column && groupOne[idx].column.showType && groupOne[idx].column.showType.value == 'MultipleInput'">
            <el-input v-model='groupOne[idx].value' :clearable='true' placeholder='请输入条件值' ></el-input>
          </div>
          <div v-else-if="groupOne[idx].column && groupOne[idx].column.showType && groupOne[idx].column.showType.value == 'SingleInput'">
            <div v-if="groupOne[idx].column && (groupOne[idx].column.javaType.value == 'Long' || groupOne[idx].column.javaType.value == 'Integer' || groupOne[idx].column.javaType.value == 'Double' || groupOne[idx].column.javaType.value == 'Float' || groupOne[idx].column.javaType.value == 'java.math.BigDecimal')">
              <number-range-input v-if="groupOne[idx].queryType == 'between' || groupOne[idx].queryType == 'not between'" v-model='groupOne[idx].value'></number-range-input>
              <number-input v-else v-model="groupOne[idx].value" :currency='groupOne[idx].column.javaType.value == "java.math.BigDecimal" ? "CNY":null' :precision="groupOne[idx].column.decimalLenth" placeholder='请输入条件值'></number-input>
            </div>
            <el-input v-else v-model='groupOne[idx].value' :clearable='true' placeholder='请输入条件值' ></el-input>
          </div>
          <div v-else-if="groupOne[idx].column && groupOne[idx].column.showType && groupOne[idx].column.showType.value == 'DatePicker'">
            <data-range-picker v-if="groupOne[idx].queryType == 'between' || groupOne[idx].queryType == 'not between'" v-model='groupOne[idx].value' type='daterange'></data-range-picker>
            <el-date-picker v-else v-model='groupOne[idx].value' placeholder='请选择日期11' type='date' value-format='yyyy-MM-dd'></el-date-picker>
          </div>
          <div v-else-if="groupOne[idx].column && groupOne[idx].column.showType && groupOne[idx].column.showType.value == 'DateTimePicker'">
            <data-range-picker v-if="groupOne[idx].queryType == 'between' || groupOne[idx].queryType == 'not between'" v-model='groupOne[idx].value' type='datetimerange'></data-range-picker>
            <el-date-picker v-else v-model='groupOne[idx].value' placeholder='请选择日期时间' type='datetime' value-format='yyyy-MM-dd HH:mm:ss'></el-date-picker>
          </div>
          <div v-else-if="groupOne[idx].column && groupOne[idx].column.showType && groupOne[idx].column.showType.value == 'TimePicker'">
            <el-time-picker v-if="groupOne[idx].queryType == 'between' || groupOne[idx].queryType == 'not between'" is-range v-model='groupOne[idx].value' range-separator='至' start-placeholder='开始时间' end-placeholder='结束时间' placeholder='选择时间范围' value-format='HH:mm:ss'></el-time-picker>
            <el-time-picker v-else  v-model='groupOne[idx].value' placeholder='请选择时间' value-format='HH:mm:ss'></el-time-picker>
          </div>
          <div v-else-if="groupOne[idx].column && groupOne[idx].column.showType && groupOne[idx].column.showType.value == 'Switch'">
            <el-switch v-model='groupOne[idx].value' active-color='#13ce66' inactive-color='#dbdfe6' :active-value='1' :inactive-value='0'></el-switch>
          </div>
          <!--<div v-else-if="groupOne[idx].column && groupOne[idx].column.showType && groupOne[idx].column.showType.value == 'IncNumber'">
            <number-range-input v-if="groupOne[idx].queryType == 'between' || groupOne[idx].queryType == 'not between'" v-model='groupOne[idx].value'></number-range-input>
            <el-input-number v-else v-model='groupOne[idx].value' controls-position='right' :controls='false' placeholder='请输入条件值'></el-input-number>
          </div>-->
          <div v-else-if="groupOne[idx].column && groupOne[idx].column.showType && (groupOne[idx].column.showType.value == 'InputNumber' || groupOne[idx].column.showType.value == 'IncNumber')">
            <number-range-input v-if="groupOne[idx].queryType == 'between' || groupOne[idx].queryType == 'not between'" v-model='groupOne[idx].value' :type="groupOne[idx].column.javaType.value" :precision="groupOne[idx].column.decimalLenth"></number-range-input>
            <number-input v-else v-model="groupOne[idx].value" :currency='groupOne[idx].column.javaType.value == "java.math.BigDecimal" ? "CNY":null' :precision="groupOne[idx].column.decimalLenth" placeholder='请输入条件值'></number-input>
          </div>
          <div v-else-if="groupOne[idx].column && groupOne[idx].column.showType && groupOne[idx].column.showType.value == 'Select'">
            <el-select v-if="groupOne[idx].queryType == 'in' || groupOne[idx].queryType == 'not in'" v-model='groupOne[idx].value' :value-key='groupOne[idx].column.fieldId' multiple filterable placeholder='请选择条件值'>
                <el-option v-for="item in pp[idx]" :key='item[groupOne[idx].column.fieldId]' :label='item[groupOne[idx].column.fieldName]' :value='item[groupOne[idx].column.fieldId]'></el-option>
            </el-select>
            <el-select v-else v-model='groupOne[idx].value' :value-key='groupOne[idx].column.fieldId'   filterable clearable placeholder='请选择条件值'>
               <el-option v-for="item in pp[idx]" :key='item[groupOne[idx].column.fieldId]' :label='item[groupOne[idx].column.fieldName]' :value='item[groupOne[idx].column.fieldId]'></el-option>
            </el-select>
          </div>
          <div v-else-if="groupOne[idx].column && groupOne[idx].column.showType && groupOne[idx].column.showType.value == 'SelectTree'">
            <el-cascader v-if="groupOne[idx].queryType == 'in' || groupOne[idx].queryType == 'not in'" v-model='groupOne[idx].value' :options="pp[idx]" :props='{value: groupOne[idx].column.fieldId, label: groupOne[idx].column.fieldName, checkStrictly: true, emitPath: false, multiple: true}' filterable clearable placeholder='请选择条件值'></el-cascader>
            <el-cascader v-else v-model='groupOne[idx].value' :options="pp[idx]" :props='{value: groupOne[idx].column.fieldId, label: groupOne[idx].column.fieldName, checkStrictly: true, emitPath: false}' filterable clearable placeholder='请选择条件值'></el-cascader>
          </div>
          <el-input v-else v-model='groupOne[idx].value' :clearable='true' placeholder='请输入条件值' ></el-input>
        </el-col>
    </el-col>
      <el-col :span='2'>
        <div v-if='idx == 1' style="margin-left:10px;margin-right:10px;">
          <el-select v-model='groupLogic'  placeholder='请选择组间逻辑符'>
              <el-option v-for='lg in logics' :key='lg.value' :label='lg.label' :value='lg.value'></el-option>
          </el-select>
        </div>
        <div v-else>&nbsp;</div>
      </el-col>
      <el-col :span='11'>
          <el-col :span='3'>
            <span v-if='idx == 0' style='text-align: center;display:block;'>二组</span>
            <el-select v-else v-model='groupTwo[idx].logic' placeholder='请选择逻辑符'>
                <el-option v-for='item in logics' :key='item.value' :label='item.label' :value='item.value'></el-option>
            </el-select>
          </el-col>
          <el-col :span='5'>
            <el-select v-model='groupTwo[idx].column' value-key='name' @change='onColumnChange(groupTwo[idx].column, groupTwo, idx)'  placeholder='请选择字段'>
                <el-option v-for="col in columns.filter(item => item['name'] != exclude)" :key='col.name' :label='col.comments' :value='col'></el-option>
            </el-select>
          </el-col>
          <el-col :span='4'>
            <el-select v-model='groupTwo[idx].queryType' @change='onQueryTypeChange(groupTwo[idx].queryType, groupTwo, idx)' placeholder='请选择操作符'>
              <el-option v-for='opr in groupTwo[idx].operations' :key='opr.value' :label='opr.label' :value='opr.value'></el-option>
            </el-select>
          </el-col>
          <el-col :span='12'>
            <div v-if="groupTwo[idx].column && groupTwo[idx].column.showType && groupTwo[idx].column.showType.value == 'MultipleInput'">
              <el-input v-model='groupTwo[idx].value' :clearable='true' placeholder='请输入条件值' ></el-input>
            </div>
            <div v-else-if="groupTwo[idx].column && groupTwo[idx].column.showType && groupTwo[idx].column.showType.value == 'SingleInput'">
              <div v-if="groupTwo[idx].column && (groupTwo[idx].column.javaType.value == 'Long' || groupTwo[idx].column.javaType.value == 'Integer' || groupTwo[idx].column.javaType.value == 'Double' || groupTwo[idx].column.javaType.value == 'Float'  || groupTwo[idx].column.javaType.value == 'java.math.BigDecimal')">
                <number-range-input v-if="groupTwo[idx].queryType == 'between' || groupTwo[idx].queryType == 'not between'" v-model='groupTwo[idx].value'></number-range-input>
                <number-input v-else v-model="groupTwo[idx].value" :currency='groupTwo[idx].column.javaType.value == "java.math.BigDecimal" ? "CNY":null' :precision="groupTwo[idx].column.decimalLenth" placeholder='请输入条件值'></number-input>
              </div>
              <el-input v-else v-model='groupTwo[idx].value' :clearable='true' placeholder='请输入条件值' ></el-input>
            </div>
            <div v-else-if="groupTwo[idx].column && groupTwo[idx].column.showType && groupTwo[idx].column.showType.value == 'DatePicker'">
              <data-range-picker v-if="groupTwo[idx].queryType == 'between' || groupTwo[idx].queryType == 'not between'" v-model='groupTwo[idx].value' type='daterange'></data-range-picker>
              <el-date-picker v-else v-model='groupTwo[idx].value' placeholder='请选择日期' type='date' value-format='yyyy-MM-dd'></el-date-picker>
            </div>
            <div v-else-if="groupTwo[idx].column && groupTwo[idx].column.showType && groupTwo[idx].column.showType.value == 'DateTimePicker'">
              <data-range-picker v-if="groupTwo[idx].queryType == 'between' || groupTwo[idx].queryType == 'not between'" v-model='groupTwo[idx].value' type='datetimerange'></data-range-picker>
              <el-date-picker v-else v-model='groupTwo[idx].value' placeholder='请选择日期时间' type='datetime' value-format='yyyy-MM-dd HH:mm:ss'></el-date-picker>
            </div>
            <div v-else-if="groupTwo[idx].column && groupTwo[idx].column.showType && groupTwo[idx].column.showType.value == 'TimePicker'">
              <el-time-picker v-if="groupTwo[idx].queryType == 'between' || groupTwo[idx].queryType == 'not between'" is-range v-model='groupTwo[idx].value' range-separator='至' start-placeholder='开始时间' end-placeholder='结束时间' placeholder='选择时间范围' value-format='HH:mm:ss'></el-time-picker>
              <el-time-picker v-else  v-model='groupTwo[idx].value' placeholder='请选择时间' value-format='HH:mm:ss'></el-time-picker>
            </div>
            <div v-else-if="groupTwo[idx].column && groupTwo[idx].column.showType && groupTwo[idx].column.showType.value == 'Switch'">
              <el-switch v-model='groupTwo[idx].value' active-color='#13ce66' inactive-color='#dbdfe6' :active-value='1' :inactive-value='0'></el-switch>
            </div>
            <!--<div v-else-if="groupTwo[idx].column && groupTwo[idx].column.showType && groupTwo[idx].column.showType.value == 'IncNumber'">
              <number-range-input v-if="groupTwo[idx].queryType == 'between' || groupTwo[idx].queryType == 'not between'" v-model='groupTwo[idx].value'></number-range-input>
              <el-input-number v-else v-model='groupTwo[idx].value' controls-position='right' :controls='false' placeholder='请输入条件值'></el-input-number>
            </div>-->
            <div v-else-if="groupTwo[idx].column && groupTwo[idx].column.showType && (groupTwo[idx].column.showType.value == 'InputNumber' || groupTwo[idx].column.showType.value == 'IncNumber')">
              <number-range-input v-if="groupTwo[idx].queryType == 'between' || groupTwo[idx].queryType == 'not between'" v-model='groupTwo[idx].value' :type="groupTwo[idx].column.javaType.value" :precision="groupTwo[idx].column.decimalLenth" ></number-range-input>
              <number-input v-else v-model="groupTwo[idx].value" :currency='groupTwo[idx].column.javaType.value == "java.math.BigDecimal" ? "CNY":null' :precision="groupTwo[idx].column.decimalLenth" placeholder='请输入条件值'></number-input>
            </div>
            <div v-else-if="groupTwo[idx].column && groupTwo[idx].column.showType && groupTwo[idx].column.showType.value == 'Select'">
              <el-select v-if="groupTwo[idx].queryType == 'in' || groupTwo[idx].queryType == 'not in'" v-model='groupTwo[idx].value' :value-key='groupTwo[idx].column.fieldId' multiple filterable placeholder='请选择条件值'>
                  <el-option v-for="item in pp[idx + 3]" :key='item[groupTwo[idx].column.fieldId]' :label='item[groupTwo[idx].column.fieldName]' :value='item[groupTwo[idx].column.fieldId]'></el-option>
              </el-select>
              <el-select v-else v-model='groupTwo[idx].value' :value-key='groupTwo[idx].column.fieldId'   filterable clearable placeholder='请选择条件值'>
                 <el-option v-for="item in pp[idx + 3]" :key='item[groupTwo[idx].column.fieldId]' :label='item[groupTwo[idx].column.fieldName]' :value='item[groupTwo[idx].column.fieldId]'></el-option>
              </el-select>
            </div>
            <div v-else-if="groupTwo[idx].column && groupTwo[idx].column.showType && groupTwo[idx].column.showType.value == 'SelectTree'">
              <el-cascader v-if="groupTwo[idx].queryType == 'in' || groupTwo[idx].queryType == 'not in'" v-model='groupTwo[idx].value' :options="pp[idx + 3]" :props='{value: groupTwo[idx].column.fieldId, label: groupTwo[idx].column.fieldName, checkStrictly: true, emitPath: false, multiple: true}' filterable clearable placeholder='请选择条件值'></el-cascader>
              <el-cascader v-else v-model='groupTwo[idx].value' :options="pp[idx + 3]" :props='{value: groupTwo[idx].column.fieldId, label: groupTwo[idx].column.fieldName, checkStrictly: true, emitPath: false}' filterable clearable placeholder='请选择条件值'></el-cascader>
            </div>
            <el-input v-else v-model='groupTwo[idx].value' :clearable='true' placeholder='请输入条件值' ></el-input>
          </el-col>
        </el-col>
    </el-row>
  </div>
</template>

<script>
import BaseUI from './baseUI'
import DataRangePicker from '@/components/DataRangePicker'
import NumberRangeInput from '@/components/NumberRangeInput'
import { columnQuery } from '@/api/common/columnQuery'
export default {
  extends: BaseUI,
  name: 'ConditionPanel',
  components: {
    DataRangePicker,
    NumberRangeInput
  },
  data() {
    return {
      pp: {
        "0": [],  "1": [], "2": [], "3": [], "4": [], "5": []
      },  // 对象字段的值选择项
      columns: [],  // 字段列表
      logics: [
        {value: 'AND', label: '且'},
        {value: 'OR', label: '或'}
      ],
      groupOne: [
        {logic: 'AND', column: null, queryType: '', value: null, operations: []},
        {logic: 'AND', column: null, queryType: '', value: null, operations: []},
        {logic: 'AND', column: null, queryType: '', value: null, operations: []}
      ],
      groupTwo: [
        {logic: 'AND', column: null, queryType: '', value: null, operations: []},
        {logic: 'AND', column: null, queryType: '', value: null, operations: []},
        {logic: 'AND', column: null, queryType: '', value: null, operations: []}
      ],
      groupLogic: 'OR'    // 两组连接逻辑运算符
    }
  },
  props: {
    value: {
      type: Object,
      default: () => {return {}}
    },
    tableId: {
      type: String
    },
    schemeId: {
      type: String
    },
    // 不作为条件的字段
    exclude: {
      type: String,
      default: () => {return null}
    },
    curNode: {
      type: Object,
      default: () => {return {}}
    },
    routerId: {
      type: String
    }
  },
  watch:{
    value(val, oldVal) {
      if(val && val != oldVal) {
        this.groupOne = this.value['groupOne']
        this.groupTwo = this.value['groupTwo']
        this.groupLogic = this.value['groupLogic']
      }
    },
    groupLogic(val, oldVal) {
      if(val!=oldVal){
        this.value['groupLogic'] = val    // 非对象，需要监控修改
      }
    },
    curNode(val, oldVal) {
      if(val == oldVal) {
        return
      }
      if(this.exclude) {
        this.watchGroupHandler(this.exclude, this.groupOne)
        this.watchGroupHandler(this.exclude, this.groupTwo)
      }
    },
    'groupOne.0.column': {
      handler(val, oldVal) {
        if(val && val != oldVal) {
          this.doInitOptions(val, this.groupOne, 0)
        }
      }
    },
    'groupOne.1.column': {
      handler(val, oldVal) {
        if(val && val != oldVal) {
          this.doInitOptions(val, this.groupOne, 1)
        }
      }
    },
    'groupOne.2.column': {
      handler(val, oldVal) {
        if(val && val != oldVal) {
          this.doInitOptions(val, this.groupOne, 2)
        }
      }
    },
    'groupTwo.0.column': {
      handler(val, oldVal) {
        if(val && val != oldVal) {
          this.doInitOptions(val, this.groupTwo, 0)
        }
      }
    },
    'groupTwo.1.column': {
      handler(val, oldVal) {
        if(val && val != oldVal) {
          this.doInitOptions(val, this.groupTwo, 1)
        }
      }
    },
    'groupTwo.2.column': {
      handler(val, oldVal) {
        if(val && val != oldVal) {
          this.doInitOptions(val, this.groupTwo, 2)
        }
      }
    },
    'groupOne.0.value': {
      handler(val, oldVal) {
        if(val) {
          if(this.groupOne[0].column) {
              this.watchValueHandler(this.groupOne, 0)
          }
        }
      }
    },
    'groupOne.1.value': {
      handler(val, oldVal) {
        if(val) {
          if(this.groupOne[1].column) {
              this.watchValueHandler(this.groupOne, 1)
          }
        }
      }
    },
    'groupOne.2.value': {
      handler(val, oldVal) {
        if(val) {
          if(this.groupOne[2].column) {
              this.watchValueHandler(this.groupOne, 2)
          }
        }
      }
    },
    'groupTwo.0.value': {
      handler(val, oldVal) {
        if(val) {
          if(this.groupTwo[0].column) {
              this.watchValueHandler(this.groupTwo, 0)
          }
        }
      }
    },
    'groupTwo.1.value': {
      handler(val, oldVal) {
        if(val) {
          if(this.groupTwo[1].column) {
              this.watchValueHandler(this.groupTwo, 1)
          }
        }
      }
    },
    'groupTwo.2.value': {
      handler(val, oldVal) {
        if(val) {
          if(this.groupTwo[2].column) {
              this.watchValueHandler(this.groupTwo, 2)
          }
        }
      }
    }
  },
  methods: {
    watchValueHandler(gpOfValue, idx) {
      let interColumnName = gpOfValue[idx].column.name

      let group = this.groupOne
      for(var i= 0 ; i < group.length; i++) {
        if(group[i].column && group[i].column.interaction) {
          let colNames = group[i].column.interaction.split(",")
          for(var j = 0; j < colNames.length; j++) {
            if(colNames[j] == interColumnName) { //
              if((group == gpOfValue && group[Math.max(idx, i)].logic == 'AND')
                || this.groupLogic == 'AND') {
                group[i].value = null
                let [This, operates] = this.constructModel(colNames, group, i)
                this.initOptions(This, operates, group[i].column, group == this.groupOne ? i : i + 3)
              }
            }
          }
        }
      }

      group = this.groupTwo
      for(var i= 0 ; i < group.length; i++) {
        if(group[i].column && group[i].column.interaction) {
          let colNames = group[i].column.interaction.split(",")
          for(var j = 0; j < colNames.length; j++) {
            if(colNames[j] == interColumnName) { //
              if((group == gpOfValue && group[Math.max(idx, i)].logic == 'AND')
                || this.groupLogic == 'AND') {
                group[i].value = null
                let [This, operates] = this.constructModel(colNames, group, i)
                this.initOptions(This, operates, group[i].column, group == this.groupOne ? i : i + 3)
              }
            }
          }
        }
      }
    },
    // interColumnName 联动列字段名
    watchGroupHandler(interColumnName, group) {
      for(var i= 0 ; i < group.length; i++) {
        if(group[i].column && group[i].column.interaction) {
          let colNames = group[i].column.interaction.split(",")
          for(var j = 0; j < colNames.length; j++) {
            if(colNames[j] == interColumnName) { // 有联动变量是依赖于左边选择框的
              group[i].value = null
              let [This, operates] = this.constructModel(colNames, group, i)
              this.initOptions(This, operates, group[i].column, group == this.groupOne ? i : i+ 3)
            }
          }
        }
      }
    },
    // colNames 联动变量数组;
    async constructModel(colNames, group, idx) {
      let model = {}
      let operates = {}
      let colIdx = group == this.groupOne ? idx : idx + 3 // 需要查询的选项的下标
      for(var i=0; i < colNames.length; i++) {
        /** 此时的 column 可能还没值 会报错 */
        let column = await this.init() && this.columns.find(item => {
          return item.name == colNames[i]
        })

        if (colNames[i] == this.exclude) {
          model[column.simpleJavaField] = this.curNode
          operates[colNames[i] + colIdx] = '='
        } else {
          // 同一组中
          model[column.simpleJavaField] = {}
          model[column.simpleJavaField][column.fieldId] = null
          let find = false
          for(var j = 0; j < group.length; j++) {
            if(j == idx) {
              continue
            }

            if (group[j].column && colNames[i] == group[j].column.name) {
              if(group[Math.max(idx, j)].logic == 'AND') {
                model[column.simpleJavaField][column.fieldId] = group[j].value
                operates[colNames[i] + colIdx] = group[j].queryType
                find = true
                break
              }
            }
          }

          // 同一组中未找到, 且组之间为AND
          if(!find && this.groupLogic == 'AND') {
            let gp = group == this.groupOne ? this.groupTwo : this.groupOne
            for(var j = 0; j < gp.length; j++) {
              if (gp[j].column && colNames[i] == gp[j].column.name) {
                model[column.simpleJavaField][column.fieldId] = gp[j].value
                operates[colNames[i] + colIdx] = gp[j].queryType
                break
              }
            }
          }
        }
      }
      return [model, operates]
    },
    init() {
      this.setLoad()
      return new Promise((resolve, reject) => {
        columnQuery(this.schemeId, this.tableId, this.exclude).then(responseData => {
          if(responseData.code == 100) {
            this.columns = responseData.data
            resolve(true)
          } else {
            this.showMessage(responseData)
          }
          this.resetLoad()
        }).catch(error => {
          this.outputError(error)
        })
      })
    },
    onQueryTypeChange(queryType, group, index) {
      // 根据操作符 和显示类型确定初始值，有待测试  变化时同步修改dataPermistion.vue onQueryTypeChange(rule)方法
      if(group[index].column && (group[index].column.showType.value == "Select" || group[index].column.showType.value == "SelectTree") && (queryType == "in" || queryType == "not in")) {
        group[index].value = []
      } else {
        group[index].value = null
      }
    },
    onColumnChange(column, group, index) {
      // 重置查询条件值
      group[index].value = null
      group[index].queryType = ''

      this.doColumnChange(column, group, index)
    },
    doColumnChange(column, group, index) {
      if(column && column.showType) {
        if(column.showType.value == 'MultipleInput') {
          group[index].operations = [
            {value: '=', label: '等于'},
            {value: '<>', label: '不等于'},
            {value: 'like', label: '包含'},
            {value: 'not like', label: '不包含'}
          ]
          group[index].queryType = 'like'
        } else if(column.showType.value == 'SingleInput') {
          if(column.javaType.value == 'Long' || column.javaType.value == 'Integer' || column.javaType.value == 'Double' || column.javaType.value == 'Float' || column.javaType.value == 'java.math.BigDecimal') {
            group[index].operations = [
              {value: '=', label: '等于'},
              {value: '<>', label: '不等于'},
              {value: 'between', label: '介于'},
              {value: 'not between', label: '不介于'}
            ]
            group[index].queryType = '='
          } else {
            group[index].operations = [
              {value: '=', label: '等于'},
              {value: '<>', label: '不等于'},
              {value: 'like', label: '包含'},
              {value: 'not like', label: '不包含'}
            ]
            group[index].queryType = 'like'
          }
        } else if(column.showType.value == 'DatePicker' || column.showType.value == 'DateTimePicker' || column.showType.value == 'TimePicker') {
          group[index].operations = [
            {value: '=', label: '等于'},
            {value: '<>', label: '不等于'},
            {value: 'between', label: '介于'},
            {value: 'not between', label: '不介于'}
          ]
          group[index].queryType = '='
        } else if(column.showType.value == 'Switch') {
          group[index].operations = [
            {value: '=', label: '等于'},
            {value: '<>', label: '不等于'}
          ]
          group[index].queryType = '='
        } else if(column.showType.value == 'IncNumber' || column.showType.value == 'InputNumber') {
          group[index].operations = [
            {value: '=', label: '等于'},
            {value: '<>', label: '不等于'},
            {value: 'between', label: '介于'},
            {value: 'not between', label: '不介于'}
          ]
          group[index].queryType = '='
        } else if(column.showType.value == 'Select' || column.showType.value == 'SelectTree') {
          if(column.name == 'ids') {
            group[index].operations = [
              {value: 'right_like', label: '含及下级'},
              {value: 'not right_like', label: '不含及下级'}
            ]
            group[index].queryType = 'right_like'
          } else {
            group[index].operations = [
              {value: '=', label: '等于'},
              {value: '<>', label: '不等于'},
              {value: 'in', label: '在列表'},
              {value: 'not in', label: '不在列表'}
            ]
            group[index].queryType = '='
          }
        }
      }
    },
    async doInitOptions(column, group, index) {
      if(column && column.showType && (column.showType.value == 'Select' || column.showType.value == 'SelectTree')) {
        let This = {}
        let operates = {}
        if(column.interaction) {
          let colNames = column.interaction.split(",")
          let [model, opr] = await this.constructModel(colNames, group, index)
          This = model
          operates = opr
        }
        this.initOptions(This, operates, column, group == this.groupOne ? index : index + 3)
      }
    },
    // This curNode； column 选择的字段； index 0~2 groupOne的，3~5 groupTwo
    initOptions(This, operates, column, index) {
      // 选项列表数据
      // console.log(column);
      let className = column.customType.genTable.className
      let apiPath = column.customType.moduleName + '/' + (column.customType.subModuleName != '' ? column.customType.subModuleName + '/': '') + className.charAt(0).toLowerCase() + className.slice(1)
      let fnOption = ''
      if(column.showType.value == 'SelectTree') {
        fnOption = require('@/api/' + apiPath)['tree' + className]
      } else {
        fnOption = require('@/api/' + apiPath)['list' + className + 'All']
      }
      // 联动参数
      let parameters = []
      if(column.parameters) {
        eval('parameters = [' + column.parameters + ']')
      }

      // 根据联动参数选择的符号替换操作符
      parameters.forEach(item => {
        let key = item.columnName+index
        if(key in operates) {
          if(operates[key]) {
            item.queryType = operates[key]
          }
        }
      })

      // 数据权限
      let search = {params: parameters, offset: 0, limit: 10, columnName: '', order: ''}
      this.pushDataPermissions(search.params, this.$route.meta.routerId, column.customType.genTable.id)

      this.pp[index] = []
      fnOption(search).then(responseData => {
        if(responseData.code == 100) {
          this.pp[index] = responseData.data
        } else {
          this.showMessage(responseData)
        }
        this.resetLoad()
      }).catch(error => {
        this.outputError(error)
      })
    },
    reset() {
      this.groupOne = [
        {logic: 'AND', column: null, queryType: '', value: null, operations: []},
        {logic: 'AND', column: null, queryType: '', value: null, operations: []},
        {logic: 'AND', column: null, queryType: '', value: null, operations: []}
      ]
      this.groupTwo = [
        {logic: 'AND', column: null, queryType: '', value: null, operations: []},
        {logic: 'AND', column: null, queryType: '', value: null, operations: []},
        {logic: 'AND', column: null, queryType: '', value: null, operations: []}
      ]
      this.groupLogic = 'OR'

      this.$emit('input', {
        groupOne: this.groupOne,
        groupTwo: this.groupTwo,
        groupLogic: this.groupLogic
      })

    }
  },
  mounted(){
    this.$nextTick(() => {
      if(this.value) {
        this.groupOne = this.value['groupOne']
        this.groupTwo = this.value['groupTwo']
        this.groupLogic = this.value['groupLogic']
      } else {  // 若数据为空，进行初始化
        this.reset()
      }

      this.$on('reset', function() {
        this.reset()
      })

      this.$on('init', function() {
        this.init()
      })
      this.init()
    })
  }
}
</script>


<style scoped lang="scss">
  .condition-panel {
    .el-row {
      margin-bottom: 5px;
      span {
        font-size: 12px;
      }
    }
  }
.el-date-editor.el-input, .el-date-editor.el-input__inner {
    width: 100%;
}

.el-select {
    display: inline-block;
    position: relative;
    width: 100%;
}
</style>
