<template>
  <div>
    <div v-for="(panel, index) in panels" :key="panel.id">
      <el-divider v-if='index>0' content-position="left">{{panel.name}}</el-divider>
      <el-row>
        <div v-for="property in panel.propertys" :key="property.id">
          <el-col :span='24' v-if="property.dictId === '8606'"> <!-- 属性集 -->
            <property-panel  v-model='propertiesData[property.id]' :propertiesId='property.bo' :cols='cols' :action='action'></property-panel>
          </el-col>
          <el-col :span='24/cols' v-if="property.dictId === '8400'"> <!-- 字符串 -->
            <el-form-item :label="property.name" prop='x' :rules="[{ required: property.isRequired=='1' ?true :false, validator: inputValidator, dataId: property.id, message: '请输入' +property.name, trigger: 'blur'}]">
              <el-input :disabled='action == "view"' :key="property.id" v-model='propertiesData[property.id]'
                        :placeholder='action == "view"? "" : "请填写" +property.name'></el-input>
            </el-form-item>
          </el-col>
          <el-col v-if="property.dictId === '8401'">  <!-- 8401文本 -->
            <el-form-item :label="property.name" prop='x' :rules="[{ required: property.isRequired=='1' ?true :false, validator: inputValidator, dataId: property.id, message: '请输入' +property.name, trigger: 'blur'}]">
              <el-input :disabled='action == "view"' v-model='propertiesData[property.id]'  type='textarea'
                        :maxlength='255'
                        :placeholder='action == "view"? "" : "请填写" +property.name'></el-input>
            </el-form-item>
          </el-col>

          <!--  8402整数   "8403" 数值  -->
          <el-col :span='24/cols' v-if="property.dictId === '8402' ||property.dictId === '8403' ">
            <el-form-item :label="property.name" prop='x' :rules="[{ required: property.isRequired=='1' ?true :false, validator: inputValidator, dataId: property.id, message: '请输入' +property.name, trigger: 'blur'}]">
              <number-input :disabled='action == "view"' v-model='propertiesData[property.id]' :precision="property.dictId === '8402'? 0: 4"
                            :placeholder='action == "view"? "" : "请填写" +property.name'></number-input>
            </el-form-item>
          </el-col>

          <!-- "8404" 金额 -->
          <el-col :span='24/cols' v-if="property.dictId === '8404' ">
            <el-form-item :label="property.name" prop='x' :rules="[{ required: property.isRequired=='1' ?true :false, validator: inputValidator, dataId: property.id, message: '请输入' +property.name, trigger: 'blur'}]">
              <number-input :disabled='action == "view"' v-model='propertiesData[property.id]' :precision="2"
                            :placeholder='action == "view"? "" : "请填写" +property.name'></number-input>
            </el-form-item>
          </el-col>

          <!-- "8405" 枚举 -->
          <el-col :span='24/cols' v-if="property.dictId === '8405' ">
            <el-form-item :label="property.name" prop='x' :rules="[{ required: property.isRequired=='1' ?true :false, validator: inputValidator, dataId: property.id, message: '请选择' +property.name, trigger: 'change'}]">
              <el-select v-model="propertiesData[property.id]" :placeholder='action == "view"? "" : "请选择" +property.name'
                         :disabled='action == "view"'>
                <el-option
                  v-for="i in property.bo.split(',')"
                  :key="i"
                  :label="i"
                  :value="i">
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>

          <!-- 字典 -->
          <el-col :span='24/cols' v-if="property.dictId  === '8406' ">
            <el-form-item :label="property.name" prop='x' :rules="[{ required: property.isRequired=='1' ?true :false, validator: inputValidator, dataId: property.id, message: '请选择' +property.name, trigger: 'change'}]">
              <el-select v-model="propertiesData[property.id]" :placeholder='action == "view"? "" : "请选择" +property.name'
                         @change="dictChange" :disabled='action == "view"'>
                <el-option
                  v-for="i in getDictItems(property.bo)"
                  :key="i.id"
                  :label="i.name"
                  :value="i.id">
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </div>
      </el-row>
    </div>
  </div>
</template>

<script>
import { listDictItemAll } from '@/api/sys/dictItem'
import { getPropertySetById } from '@/api/sys/propertySet'
import BaseUI from '@/views/components/baseUI'
export default {
  extends: BaseUI,
  name: 'property-panel',
  data() {
    let inputValidator= (rule, value, callback) => {
      if(rule.required) {
        let theValue = this.propertiesData[rule.dataId]
        if(!theValue) {
          let msg = rule.message | '不能为空！'
          return callback(new Error(msg))
        }
      }
      callback()
    }
    return {
      asyncOptions: {},             // 保存从后台获取的异步选项数据
      panels:[],                    // 属性集分组数组
      propertiesData: {},
      inputValidator: inputValidator
    }
  },
  props: {
    // 接受外部v-model传入的值
    value: {
      type: String
    },
    // 属性集Id
    propertiesId: {
      type: String | Number
    },
    //属性定义json字符串
    propertyDef: {
      type: String
    },
    // 操作类型--编辑、查看等
    action: {
      type: String,
      default: () => { return "view"}
    },
    // 编辑框布局列数
    cols: {
      type: Number | String,
      default: () => { return 1}
    }
  },
  watch:{
    value(val, oldVal) {
      if(val != oldVal) {
        if(this.value) {
          this.propertiesData = JSON.parse(this.value)
        } else {
          this.propertiesData = {}
        }
      }
    },
    propertiesId(val, oldVal) {
      if(val != oldVal) {
        this.getDef()
      }
    },
    propertyDef(val, oldVal) {
      if(val != oldVal) {
        this.getDef()
      }
    },
    propertiesData: {
      handler(newName, oldName) {
          this.$emit('input', JSON.stringify(this.propertiesData))
      },
      deep: true
    }
  },
  methods: {
    dictChange(){
      this.$forceUpdate()
    },
    init(propertyDef) {
      this.panels = JSON.parse(propertyDef)
      // 初始化默认值
      if(this.panels && JSON.stringify(this.propertiesData) === "{}"){
        let obj = {};
        for(let panel of this.panels){
            for(let property of panel.propertys){
                if(property.dictId === "8606"){  //当为属性集时  忽略掉默认值
                  obj[property.id] = null;
                }else{
                  obj[property.id] =  property.defaultValue
                }
            }
        }
        this.propertiesData = obj
      }
    },
    getDef() {
      this.panels = []
      if(this.propertyDef) {
        this.init(this.propertyDef)
      } else if(this.propertiesId) {
        getPropertySetById(this.propertiesId).then(responseData => {
          if(responseData.code === "100") {
            this.init(responseData.data.propertiesDef)
          } else {
            this.showMessage(responseData)
          }
        }).catch(error => {
          this.outputError(error)
        })
      }
    },
    getDictItems(typeId) {
      let key = 'dict_' + typeId   // 异步数据保存key
      if(this.asyncOptions.hasOwnProperty(key) == false) {
        this.asyncOptions[key] = []       // 初始化异步数据
        listDictItemAll({
          params: [{'columnName':'dict_type_id', 'queryType': '=', 'value': typeId}]
         }).then(responseData => {
          if(responseData.code == 100) {
            this.asyncOptions[key].push.apply(this.asyncOptions[key], responseData.data)
            this.$forceUpdate()
          } else {
            this.showMessage(responseData)
          }
        }).catch(error => {
          this.outputError(error)
        })

      }
      return this.asyncOptions[key]
    }
  },
  mounted: function() {
    // 初始值
    if(this.value) {
      this.propertiesData = JSON.parse(this.value)
    }
    // 根据 propertiesId 获取属性集定义
    this.getDef()
  }
}
</script>

