<template>
    <div>
      <quillEditor v-model="content"  @input="updateValue"  :disabled='disabled'  class="editor"  ref="vueQuillEditor" :options="editorOption"
                   @blur="onEditorBlur($event)" @focus="onEditorFocus($event)" @change="onEditorChange($event)">
      </quillEditor>
    </div>
</template>
<script>
  import 'quill/dist/quill.core.css'
  import 'quill/dist/quill.snow.css'
  import 'quill/dist/quill.bubble.css'
  import {quillEditor}from 'vue-quill-editor';
  import  {titleConfig,toolbarOptions} from "./tool"
    export default {
        props: {
          value: {
            type: String,
            default: null,
          },
          disabled:{
            type: Boolean,
            default: true
          }
        },
        name: "editor",
        components:{quillEditor},
        data(){
          return{
            content:this.value,
            editorOption: {
              theme: "snow",
              placeholder: "请输入",
              modules: {
                toolbar: {
                  container: toolbarOptions,
                }
              }
            },
          }
        },
      watch:{
          'value':{
              handler(newVal){
                this.content = newVal;
            },
          }
      },
      computed: {
        editor() {
          return this.$refs.vueQuillEditor.quill;
        }
      },
      mounted(){
        document.getElementsByClassName('ql-editor')[0].dataset.placeholder = '请输入';
        for(let item of titleConfig){
          let tip = document.querySelector('.quill-editor '+ item.Choice);
          if (!tip) continue;
          tip.setAttribute('title',item.title)
        }
      },
      methods:{
        onEditorBlur(){},  // 失去焦点事件
        onEditorFocus(event){
        }, // 获得焦点事件
        onEditorChange(){  // 内容改变事件

        },
        updateValue(value) {
          this.$emit("input", this.content);
        },
      }
    }
</script>

<style>
  .editor {
    line-height: normal;
    height: 300px;
  }
  .ql-snow .ql-tooltip[data-mode=link]::before {
    content: "请输入链接地址:";
  }
  .ql-snow .ql-tooltip.ql-editing a.ql-action::after {
    border-right: 0px;
    content: '保存';
    padding-right: 0px;
  }

  .ql-snow .ql-tooltip[data-mode=video]::before {
    content: "请输入视频地址:";
  }

  .ql-snow .ql-picker.ql-size .ql-picker-label::before,
  .ql-snow .ql-picker.ql-size .ql-picker-item::before {
    content: '14px';
  }
  .ql-snow .ql-picker.ql-size .ql-picker-label[data-value=small]::before,
  .ql-snow .ql-picker.ql-size .ql-picker-item[data-value=small]::before {
    content: '10px';
  }
  .ql-snow .ql-picker.ql-size .ql-picker-label[data-value=large]::before,
  .ql-snow .ql-picker.ql-size .ql-picker-item[data-value=large]::before {
    content: '18px';
  }
  .ql-snow .ql-picker.ql-size .ql-picker-label[data-value=huge]::before,
  .ql-snow .ql-picker.ql-size .ql-picker-item[data-value=huge]::before {
    content: '32px';
  }

  .ql-snow .ql-picker.ql-header .ql-picker-label::before,
  .ql-snow .ql-picker.ql-header .ql-picker-item::before {
    content: '文本';
  }
  .ql-snow .ql-picker.ql-header .ql-picker-label[data-value="1"]::before,
  .ql-snow .ql-picker.ql-header .ql-picker-item[data-value="1"]::before {
    content: '标题1';
  }
  .ql-snow .ql-picker.ql-header .ql-picker-label[data-value="2"]::before,
  .ql-snow .ql-picker.ql-header .ql-picker-item[data-value="2"]::before {
    content: '标题2';
  }
  .ql-snow .ql-picker.ql-header .ql-picker-label[data-value="3"]::before,
  .ql-snow .ql-picker.ql-header .ql-picker-item[data-value="3"]::before {
    content: '标题3';
  }
  .ql-snow .ql-picker.ql-header .ql-picker-label[data-value="4"]::before,
  .ql-snow .ql-picker.ql-header .ql-picker-item[data-value="4"]::before {
    content: '标题4';
  }
  .ql-snow .ql-picker.ql-header .ql-picker-label[data-value="5"]::before,
  .ql-snow .ql-picker.ql-header .ql-picker-item[data-value="5"]::before {
    content: '标题5';
  }
  .ql-snow .ql-picker.ql-header .ql-picker-label[data-value="6"]::before,
  .ql-snow .ql-picker.ql-header .ql-picker-item[data-value="6"]::before {
    content: '标题6';
  }

  .ql-snow .ql-picker.ql-font .ql-picker-label::before,
  .ql-snow .ql-picker.ql-font .ql-picker-item::before {
    content: '标准字体';
  }
  .ql-snow .ql-picker.ql-font .ql-picker-label[data-value=serif]::before,
  .ql-snow .ql-picker.ql-font .ql-picker-item[data-value=serif]::before {
    content: '衬线字体';
  }
  .ql-snow .ql-picker.ql-font .ql-picker-label[data-value=monospace]::before,
  .ql-snow .ql-picker.ql-font .ql-picker-item[data-value=monospace]::before {
    content: '等宽字体';
  }
  #disabledMsg.el-button.is-disabled, .el-button.is-disabled:focus, .el-button.is-disabled:hover{
    color: #bfd9d8;
    background-color: #eef6f6;
    border-color: #d1e5e5;
  }
</style>
