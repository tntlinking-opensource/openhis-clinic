'use strict';

var getBusinessObject = require('bpmn-js/lib/util/ModelUtil').getBusinessObject;

var escapeHTML = require('bpmn-js-properties-panel/lib/Utils').escapeHTML;

var domQuery = require('min-dom').query;

var entryFieldDescription = require('bpmn-js-properties-panel/lib/factory/EntryFieldDescription');

var cmdHelper = require('bpmn-js-properties-panel/lib/helper/CmdHelper');

function ensureNotNull(prop) {
  if (!prop) {
    throw new Error(prop + ' must be set.');
  }

  return prop;
};


var setDefaultParameters = function(options) {

  // default method to fetch the current value of the input field
  var defaultGet = function(element) {
    var bo = getBusinessObject(element),
        res = {},
        prop = ensureNotNull(options.modelProperty);
    res[prop] = bo.get(prop);

    return res;
  };

  // default method to set a new value to the input field
  var defaultSet = function(element, values) {
    var res = {},
        prop = ensureNotNull(options.modelProperty);
    if (values[prop] !== '') {
      res[prop] = values[prop];
    } else {
      res[prop] = undefined;
    }

    return cmdHelper.updateProperties(element, res);
  };

  // default validation method
  var defaultValidate = function() {
    return {};
  };

  return {
    id : options.id,
    description : (options.description || ''),
    get : (options.get || defaultGet),
    set : (options.set || defaultSet),
    validate : (options.validate || defaultValidate),
    html: ''
  };
};


var userField = function(options) {

  var defaultParameters = setDefaultParameters(options);

  // Default action for the button next to the input-field
  var defaultButtonAction = function(element, inputNode) {
    var input = domQuery('input[name="' + options.modelProperty + '"]', inputNode);
    input.value = '';

    return true;
  };

  // default method to determine if the button should be visible
  var defaultButtonShow = function(element, inputNode) {
    var input = domQuery('input[name="' + options.modelProperty + '"]', inputNode);

    return input.value !== '';
  };


  var resource = defaultParameters,
      label = options.label || resource.id,
      dataValueLabel = options.dataValueLabel,
      buttonLabel = (options.buttonLabel || 'X'),
      actionName = (typeof options.buttonAction != 'undefined') ? options.buttonAction.name : 'clear',
      actionMethod = (typeof options.buttonAction != 'undefined') ? options.buttonAction.method : defaultButtonAction,
      showName = (typeof options.buttonShow != 'undefined') ? options.buttonShow.name : 'canClear',
      showMethod = (typeof options.buttonShow != 'undefined') ? options.buttonShow.method : defaultButtonShow,
      description = options.description,
      doSelectUser = options.doSelectUser;

  resource.html =
    '<label for="camunda-' + escapeHTML(resource.id) + '" ' +
      (dataValueLabel ? 'data-value="' + escapeHTML(dataValueLabel) + '"' : '') + '>'+ escapeHTML(label) +'</label>' +
    '<div class="bpp-field-wrapper" ' +
     
      '>' +
      '<button data-action="doSelectUser" ' +
      '>...</button>' + 
      '<input id="camunda-' + escapeHTML(resource.id) + '" type="text" name="' + escapeHTML(options.modelProperty) + '" ' +
        ' onfocus=this.blur() data-action="doSelectUser" style="margin-left: 24px;" autocomplete="off" />' +
      '<button class="' + escapeHTML(actionName) + '" data-action="' + escapeHTML(actionName) + '" data-show="' + escapeHTML(showName) + '" ' +
        '<span>' + escapeHTML(buttonLabel) + '</span>' +
      '</button>' +
    '</div>';

  // add description below text input entry field
  if (description) {
    resource.html += entryFieldDescription(description);
  }

  resource[actionName] = actionMethod;
  resource[showName] = showMethod;
  resource['doSelectUser'] = doSelectUser;

  resource.cssClasses = ['bpp-textfield'];

  return resource;
};

module.exports = userField;
