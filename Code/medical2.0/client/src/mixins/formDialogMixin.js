/**
 * Form Dialog Mixin
 *
 * Encapsulates common dialog form logic to eliminate boilerplate in 50+ Form.vue files.
 * Works alongside BaseUI (which is `extends`-ed, not mixed in).
 *
 * Usage:
 *   import formDialogMixin from '@/mixins/formDialogMixin'
 *
 *   export default {
 *     extends: BaseUI,
 *     mixins: [formDialogMixin],
 *     name: 'role-form',
 *     components: { OperationIcon },
 *     data() {
 *       return {
 *         entityName: 'Role',          // required: event suffix, e.g. openViewRoleDialog
 *         entityLabel: '角色',          // optional: used in dialog titles
 *         formRefName: 'roleForm',     // optional: auto clearValidate in onDialogOpen
 *         formRules: { ... },          // validation rules
 *       }
 *     },
 *     methods: {
 *       initFormModel(parent) {        // optional: return default model (parent data for tree entities)
 *         return { code: '', name: '', ... }
 *       },
 *       initOptions(model) { ... },    // optional: load dropdowns / cascader options
 *       doSave() {                     // required: call save API
 *         this.setLoad()
 *         saveRole(this.bizFormModel).then(res => {
 *           this.handleSaveResponse(res)
 *         }).catch(err => {
 *           this.handleSaveError(err)
 *         })
 *       }
 *     }
 *   }
 */
export default {
  props: {
    permission: {
      type: Object,
      default: () => ({})
    }
  },

  data() {
    return {
      // ---- subclass should define these ----
      entityName: '',       // e.g. 'Role'   — event name suffix
      entityLabel: '',      // e.g. '角色'    — dialog title label
      formRefName: '',      // e.g. 'roleForm' — form ref for clearValidate

      // ---- managed by mixin ----
      bizFormModel: {},
      tabIndex: '1',
      flage: false,
      dialogProps: {
        visible: false,
        action: '',
        title: ''
      }
    }
  },

  created() {
    const name = this.entityName
    if (!name) return

    // Register direct methods so parents can call this.$refs.form.openViewXxxDialog(data)
    this['openView' + name + 'Dialog'] = (data) => this.openDialog('view', data)
    this['openEdit' + name + 'Dialog'] = (data) => this.openDialog('edit', data)
    this['openAdd' + name + 'Dialog'] = (data) => this.openDialog('add', data)
    this['openCopy' + name + 'Dialog'] = (data) => this.openDialog('copy', data)
  },

  methods: {
    /**
     * Unified open-dialog handler.
     * @param {'view'|'edit'|'add'|'copy'} action
     * @param {Object} [data] - entity for view/edit/copy, parent entity for add
     */
    openDialog(action, data) {
      this.dialogProps.action = action
      this.dialogProps.title = this.getTitle(action)

      if (action === 'add') {
        // For tree entities, data is the parent node
        this.bizFormModel = this.initFormModel(data)
      } else if (action === 'copy') {
        this.bizFormModel = { ...data }
        this.bizFormModel.id = null
      } else {
        // view / edit — data is the full entity
        this.bizFormModel = data
      }

      if (this.initOptions) {
        this.initOptions(this.bizFormModel)
      }

      this.tabIndex = '1'
      this.dialogProps.visible = true
    },

    /**
     * Build dialog title from action and entityLabel.
     */
    getTitle(action) {
      const label = this.entityLabel || ''
      const map = {
        view: '查看' + label,   // 查看
        edit: '修改' + label,   // 修改
        add:  '添加' + label,   // 添加
        copy: '添加' + label    // 添加
      }
      return map[action] || ''
    },

    /**
     * Form submit — validate then call doSave().
     * @param {string} formName - the form ref name in template
     */
    onSubmit(formName) {
      this.flage = true
      this.$refs[formName].validate((valid) => {
        if (valid) {
          this.doSave()
        } else {
          this.flage = false
        }
      })
    },

    /**
     * Called after a successful save API response.
     * Resets the submit guard, hides dialog, notifies parent.
     */
    handleSaveResponse(responseData) {
      this.flage = false
      if (responseData.code === 100) {
        this.dialogProps.visible = false
        this.$emit('save-finished')
      } else {
        this.showMessage(responseData)
      }
      this.resetLoad()
    },

    /**
     * Called when save API call fails.
     */
    handleSaveError(error) {
      this.flage = false
      this.outputError(error)
    },

    /**
     * Switch from view mode to edit mode.
     */
    switchEdit() {
      this.dialogProps.action = 'edit'
      this.dialogProps.title = this.getTitle('edit')
      if (this.initOptions) {
        this.initOptions(this.bizFormModel)
      }
    },

    /**
     * Close the dialog and reset guard flag.
     */
    onDialogClose() {
      this.dialogProps.visible = false
      this.flage = false
    },

    /**
     * After dialog opens — auto clearValidate on the form ref.
     * Override if you need additional logic (e.g. focus a field).
     */
    onDialogOpen() {
      this.$nextTick(() => {
        const ref = this.formRefName
        if (ref && this.$refs[ref]) {
          this.$refs[ref].clearValidate()
        }
      })
    },

    // ---- abstract / overridable ----

    /**
     * Return a fresh model object.
     * For tree entities, parent is the parent node passed from openAdd.
     * @param {Object} [parent] - parent entity (tree structures)
     * @returns {Object}
     */
    initFormModel(parent) {
      return {}
    },

    /**
     * Load dropdown / cascader options after dialog opens.
     * @param {Object} model - the current bizFormModel
     */
    initOptions(model) {
      // subclass overrides
    },

    /**
     * Subclass MUST override: call the save API and wire up response.
     * Typical implementation:
     *
     *   doSave() {
     *     this.setLoad()
     *     saveXxx(this.bizFormModel).then(res => {
     *       this.handleSaveResponse(res)
     *     }).catch(err => {
     *       this.handleSaveError(err)
     *     })
     *   }
     */
    doSave() {
      console.warn('formDialogMixin: doSave() not implemented')
    }
  }
}
