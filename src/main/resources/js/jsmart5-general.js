/*
 * JSmart5 - Java Web Development Framework
 * Copyright (c) 2014, Jeferson Albino da Silva, All rights reserved.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this library. If not, see <http://www.gnu.org/licenses/>.
*/

var Jsmart5 = (function() {

	var TAG_INIT = "j0";
	var DIALOG_OPEN = 'open()';
	var DIALOG_CLOSE = 'close()';
	var RESET_PATH = '#jsmart_reset_ajax_path';
	var REDIRECT_PATH = '#jsmart_redirect_ajax_path';
	var MESSAGES_EXEC = 'jsmart_messages';
	var MULTI_SELECT_ALL = "_multi_select_all";
	var MULTI_SELECT_ITEM_ID = "_multi_select_item_";
	var EDIT_CELL_START_ITEM_ID = "_edit_cell_start_item_";
	var EDIT_CELL_CONFIRM_ITEM_ID = "_edit_cell_confirm_item_";
	var EDIT_CELL_CANCEL_ITEM_ID = "_edit_cell_cancel_item_";
	var EDIT_CELL_COLUMN_ITEM_ID = "_edit_cell_column_item_";
	var SELECT_ROW_ITEM_ID = "_select_row_item_";
	var SWITCH_INPUT = "_switch_input";
	var SWITCH_BUTTON = "_switch_button";
	var SWITCH_SPAN_ON = "_switch_span_on";
	var SWITCH_SPAN_OFF = "_switch_span_off";
	var BALLOON_HOLDER = "_balloon_holder";
	var KEYPAD_HOLDER = "_keypad_holder";
	var BALLOON_WRAPPER = "_balloon_wrapper";
	var CAROUSEL_SLIDE = "_slide_";
	var CAROUSEL_TIMERS = [];
	var PANEL_COLLAPSE = "_panel_collpase";
	var PANEL_CONTENT = "_panel_content";
	var TAB_INDEX = "_tab_index";
	var PROGRESS_FRAME = "_progress_frame";
	var PROGRESS_PERCENT = "_progress_percent";
	var PROGRESS_INPUT = "_progress_input";
	var PROGRESS_TIMERS = [];
	var RANGE_FRAME = "_range_frame";
	var RANGE_INPUT = "_range_input";
	var RANGE_VALUE = "_range_value";
	var DATE_FORMAT = '_date_format';
	var AUTOCOMPLETE_VALUES = '_complete_values';
	var AUTOCOMPLETE_TIMERS = [];
	var AUTOCOMPLETE_CHARS = [];
	var TABLE_FILTER_TIMERS = [];

	$(function () {
		initCheckbox();
		initPlaceHolders();
	});

	// begin public interface
	return {

		// Ajax ajax={'method': '', 'action': '','params': [{'name': '', 'value': ''}],'update': '','before': '','exec': ''}
		ajax: function (async, element, timeout) {
			doAjax(async, element, timeout);
		},

		//Link ajax={'method': '', 'action': '','url': '','update': '','before': '','exec': ''}
		link: function (async, element) {
			doButton(async, element);
		},
		
		linkDropDown: function (element) {
			doLinkDropDown(element);
		},

		// Button ajax={'method': '', 'action': '','update': '','before': '','exec': ''}
		button: function (async, element) {
			doButton(async, element);
		},

		buttonDropDown: function (element) {
			doButtonDropDown(element);
		},

		// Rest ajax={'method': '','content': '','bodyRoot': '','crossdomain': '', 'jsonp': '', 'jsoncallback': '','endpoint': '','params':[{'name': '', 'value': ''}],'before': '','success': '','error': ''}
		rest: function (async, element, timeout) {
			doRest(async, element, timeout);
		},
		
		buttonRestArray: function (id, operation) {
			doButtonRestArray(id, operation);
		},
			
		load: function (id) {
			doLoad(id);
		},
		
		upload: function (id) {
			doUpload(id);
		},
			
		validate: function (id) {
			return doValidate(id);
		},
		
		execute: function(a, exec) {
			doExecute(a, exec);
		},
		
		//List ajaxOutside={'params':[{'name': '', 'value': ''}]}
		list: function (async, element) {
			doList(async, element);
		},
		
		// Table ajax={'name':'', 'action': '', 'first': '', 'sort': '', 'filter': '', 'update': ''}
		table: function (async, id, element) {
			doTable(async, id, element);
		},
		
		// Table ajaxEval={'name': '', 'action': '', 'multi': '', 'indexes': '', 'first': '', 'size': '', 'sort': '', 'filter': '', 'update': ''}
		tableSelect: function (async, id, element) {
			doTableSelect(async, id, element);
		},
		
		// Table ajaxScroll{'name':'', 'action':'', 'first':'', 'end':'', 'filter':'', 'sort':''}
		tableScroll: function (id) {
			doTableScroll(id);
		},
		
		// Selects odd elements, zero-indexed, counter-intuitively, :odd selects the second element, fourth element, and so on within the matched set.
		tableRowExpand: function (id) {
			doTableRowExpand(id);
		},
		
		// Table ajax={'varname': '', 'name': '', 'action': '', 'index': '', 'first': '', 'size': '', 'sort': '', 'filter': '', 'update': ''}
		tableEdit: function (async, id, element) {
			doTableEdit(async, id, element);
		},
		
		tableEditStart: function (id, item, index, first) {
			doTableEditStart(id, item, index, first);
		},
		
		tableEditCancel: function (id, item, index, first) {
			doTableEditCancel(id, item, index, first);
		},
		
		// Tab ajax={'ajax':'', 'collapsible':'', 'position':'', 'hover':''}
		tab: function (id) {
			doTab(id);
		},
		
		select: function (async, id) {
			doSelect(async, id);
		},

		backupNumber: function (input) {
			input.attr('nbackup', input.val());
		},

		number: function (input) {
			if (!/^[0-9]+$/.test(input.val())) { 
				input.attr('value', input.attr('nbackup'));
			}
		},

		backupDate: function (input) {
			input.attr('dbackup', input.val());
		},

		date: function (input) {
			doDate(input);
		},

		panel: function (id) {
			doPanel(id);
		},
		
		checkbox: function (input) {
			doCheckbox(input);
		},
		
		groupItem: function (async, name, element) {
			doGroupItem(async, name, element);
		},
		
		xswitch: function (id, async, ajax) {
			doSwitch(id, async, ajax);
		},
		
		resetSwitch: function (id) {
			resetSwitch(id);
		},
		
		balloon: function (target, position, opened, length, message) {
			doBalloon(target, position, opened, length, message);
		},

		carousel: function (id) {
			doCarousel(id);
		},

		message: function (messages, options) {
			doMessage(messages, options);
		},
		
		menu: function (id) {
			doMenu(id);
		},

		// Table ajax={'ajax': '', 'max': '', 'min': '', 'interval': '', 'complete': '', 'callback': ''}
		progress: function (id) {
			doProgress(id);
		},

		// Table ajax={'ajax': '', 'max': '', 'min': '', 'step': '', 'callback': ''}
		range: function (id) {
			doRange(id);
		},
		
		autocomplete: function (async, id, event) {
			doAutoComplete(async, id, event);
		},

		resetAutocomplete: function (id) {
			resetAutocomplete(id);
		},

		search: function (async, id, event) {
			return doSearch(async, id, event);
		},
		
		resetSearch: function (id) {
			resetSearch(id);
		},
		
		removeSearch: function (id, close) {
			doRemoveSearch(id, close);
		},

		tree: function (id) {
			doTree(id);
		}
	};
	// end public interface

	function doAjax(async, element, timeout) {
		if ($(element) && $(element).attr('ajax')) {
			if (timeout && timeout > 0) {
				setTimeout(function() {doAjax(async, element, null);}, timeout);
			} else {
				var ajax = $.parseJSON($(element).attr('ajax'));
	
				if (ajax.method) {
					var options = getAjaxOptions(async, ajax);
					var closestForm = $(element).closest('form');
	
					if (ajax.method == 'post') {
						var postParam = getAjaxParams(element);
	
						var outsideParams = getAjaxOutsideParams(element);
						for (var i = 0; i < outsideParams.length; i++) {
							postParam.push(outsideParams[i]);
						}
	
						// This is only works for ajax tag inside table tag
						var evalParams = getAjaxEvalParams(element);
						for (var i = 0; i < evalParams.length; i++) {
							postParam.push(evalParams[i]);
						}
	
						if (closestForm && closestForm.length > 0) {
							if (!doValidate($(closestForm).attr('id'))) {
								return;
							}
						} else {
							var elementParam = getElementParam(element, false);
							for (var i = 0; i < elementParam.length; i++) {
								postParam.push({name: elementParam[i].name, value: elementParam[i].value});
							}
							postParam = $.param(postParam);
						}
	
						options.data = postParam;
					}
	
					if (ajax.method == 'post' && closestForm && closestForm.length > 0) {
						doFormPlaceHolders(closestForm);
						$(closestForm).ajaxSubmit(options);
					} else {
						$.ajax(options);
					}
				} else {
					if (ajax.before) {
						doExecute(null, ajax.before);
					}
					if (ajax.exec) {
						doExecute(null, ajax.exec);
					}
				}
			}
		}
	}

	function doButton(async, element) {
		if ($(element) && $(element).attr('ajax')) {
			var ajax = $.parseJSON($(element).attr('ajax'));
	
			if (ajax.method) {
				var options = getAjaxOptions(async, ajax);
				var closestForm = $(element).closest('form');
				
				if (ajax.method == 'post') {
					var postParam = null;
	
					if (closestForm && closestForm.length > 0) {
						if (!doValidate($(closestForm).attr('id'))) {
							return;
						}
						postParam = getAjaxParams(element);
					} else {
						postParam = $.param(getAjaxParams(element));
					}
					options.data = postParam;
				}
	
				if (ajax.method == 'post' && closestForm && closestForm.length > 0) {
					doFormPlaceHolders(closestForm);
					$(closestForm).ajaxSubmit(options);
				} else {
					$.ajax(options);
				}
			} else {
				if (ajax.before) {
					doExecute(null, ajax.before);
				}
				if (ajax.exec) {
					doExecute(null, ajax.exec);
				}
			}
		}
	}

	function doRest(async, element, timeout) {
		if ($(element) && $(element).attr('ajax')) {
			if (timeout && timeout > 0) {
				setTimeout(function() {doRest(async, element, null);}, timeout);
			} else {

				var ajax = $.parseJSON($(element).attr('ajax'));
				var queryParams = getRestQueryParams(element);
	
				var options = {
					type: ajax.method,
				    url: ajax.endpoint + (queryParams.length > 0 ? (ajax.endpoint.indexOf('?') >= 0 ? '&' : '?') + queryParams : ''),
				    complete: function (xhr, status) {
						jQuery.event.trigger('ajaxStop');
					},
					async: async
				};
	
				// callback settings
				if (ajax.before && ajax.before.length > 0) {
					options.beforeSend = eval(ajax.before);
				}
				if (ajax.success && ajax.success.length > 0) {
					options.success = eval(ajax.success);
				}
				if (ajax.error && ajax.error.length > 0) {
					options.error = eval(ajax.error);
				}

				options.dataType = ajax.content;
				options.contentType = 'application/' + ajax.content;

				// jsonp settings
				if (ajax.crossdomain) {
					options.crossDomain = ajax.crossdomain;
				}
				if (options.crossDomain || (ajax.jsonp && ajax.jsonp.length > 0) || (ajax.jsonpcallback && ajax.jsonpcallback.length > 0)) {
					options.dataType = 'jsonp';
				}
				if (ajax.jsonp && ajax.jsonp.length > 0) {
					options.jsonp = ajax.jsonp;
				}
				if (ajax.jsonpcallback && ajax.jsonpcallback.length > 0) {
					options.jsonpCallback = ajax.jsonpcallback;
				}

				// body settings
				if (ajax.method != 'get' && ajax.method != 'head') {
					if (ajax.content == 'json') {
						options.data = getRestJsonBody(element, ajax.bodyRoot);

					} else if (ajax.content == 'xml') {
						options.data = getRestXmlBody(element, ajax.bodyRoot);
					}
				}

				$.ajax(options);
			}
		}
	}

	function doButtonRestArray(id, operation) {
		if (id && id.length > 0) {
			var arrayElement = $(getId(id));
			var arrayLength = arrayElement.parent().find('[id^="' + id + '"]').length;

			if (operation == 'add') {
				var maxItems = null;
				if (arrayElement.attr('maxItems')) {
					maxItems = parseInt(arrayElement.attr('maxItems'));
				}

				if (!maxItems || arrayLength < maxItems) {
					var cloneElement = arrayElement.clone();
					cloneElement.attr('id', id + '_' + arrayLength);

					cloneElement.find('*[id]').each(function(index) {
						$(this).attr('id', $(this).attr('id') + '_' + arrayLength);
					});

					if (arrayLength == 1) {
						arrayElement.after(cloneElement);
					} else {
						$(getId(id + '_' + (arrayLength - 1))).after(cloneElement);
					}
				}
			} else if (operation == 'remove') {
				if (arrayLength > 1) {
					$(getId(id + '_' + (arrayLength - 1))).remove();
				}
			}
		}
	}

	function doLoad(id) {
		if (id && id.length > 0) {
			var loadElement = $(getId(id)); 
			loadElement.hide();
			loadElement.ajaxStart(function(){
				$(this).show();
			}).ajaxStop(function(){
				$(this).hide();}
			);
		}
	}

	function doUpload(id) {
		if (id && id.length > 0) {
			var uploadElement = $(getId(id));

			uploadElement.parent().find('span[validateref]').each(function(index) {
				$(this).remove();
			});

			var maxFileSize = uploadElement.attr('maxFileSize');
			var maxSizeMsg = uploadElement.attr('maxSizeMsg');

			if (uploadElement[0].files.length > 0) {
				var file = uploadElement[0].files[0];
				var fileSize = file.size || file.fileSize;

				if (parseInt(fileSize) > parseInt(maxFileSize)) {
					uploadElement.val('');
					if (maxSizeMsg && maxSizeMsg.length > 0) {
						uploadElement.after($('<span validateref=""></span>').addClass("jsmart5_validate").text(maxSizeMsg));
					}
				}
			}
		}
	}

	function doValidate(id) {
		var validated = true;
		if (id && id.length > 0) {
			var validateElement = $(getId(id));
			var checkgroups = new Array();
	
			validateElement.find('span[validateref]').each(function(index) {
				$(this).remove();
			});
	
			validateElement.find('*[validatedrequired]').each(function(index) {
				var name = $(this).attr("name");

				if (name.indexOf(TAG_INIT) >= 0) {
					var message = $(this).attr('validatemessage');
					var value = getElementParam($(this), true);
	
					if ($(this).is('input') && $(this).attr('checkgroup')) {
						if (!contains(checkgroups, name)) {
							checkgroups[checkgroups.length] = name;

							$(this).closest('table').find('div label').each(function(index) {
								$(this).removeClass('jsmart5_validate_target');
							});
	
							if (value.length == 0 || !value[0].value || value[0].value.length == 0) {
								warnValidate($(this), message, 'checkgroup');
								validated = false;
							}
						}
					} else if ($(this).is('input') && $(this).attr('radiogroup')) {
						if (!contains(checkgroups, name)) {
							checkgroups[checkgroups.length] = name;

							$(this).closest('table').find('div label').each(function(index) {
								$(this).removeClass('jsmart5_validate_target');
							});

							if (value.length == 0 || !value[0].value || value[0].value.length == 0) {
								warnValidate($(this), message, 'radiogroup');
								validated = false;
							}
						}
	
					} else if ($(this).is('input:checkbox')) {

						$(this).closest('div').find('>label').removeClass('jsmart5_validate_target');

						if (value.length == 0 || !value[0].value || value[0].value == 'false') {
							warnValidate($(this), message, 'checkbox');
							validated = false;
						}
					} else {
						var type = $(this).is('input') && $(this).attr('switch') ? 'switch' : 
								   $(this).is('input') && ($(this).attr('autocomplete') || $(this).attr('search')) ? 'inputgroup' : 'general';

						if (type == 'switch') {
							$(this).closest('div').removeClass('jsmart5_validate_target');

						} else if (type == 'inputgroup') {
							$(this).parents('div:eq(2)').removeClass('jsmart5_validate_target');

						} else {
							$(this).removeClass('jsmart5_validate_target');
						}

						if (value.length > 0 && value[0].value && value[0].value.length > 0) {

							if (isString(value[0].value) && $.trim(value[0].value).length == 0) {
								warnValidate($(this), message, type);
								validated = false;
	
							} else {
								var validateMinLength = $(this).attr('validateminlength');
								if (validateMinLength && value[0].value.length < validateMinLength) {
									warnValidate($(this), message, type);
									validated = false;
								}
	
								var validateMaxLength = $(this).attr('validatemaxlength');
								if (validateMaxLength && value[0].value.length > validateMaxLength) {
									warnValidate($(this), message, type);
									validated = false;
								}
							}
						} else {
							warnValidate($(this), message, type);
							validated = false;
						}
					}
				}
			});
		}
		return validated;
	}
	
	function warnValidate(element, message, type) {
		if (type == 'radiogroup' || type == 'checkgroup') {
			element.closest('table').find('div label').each(function(index) {
				$(this).addClass('jsmart5_validate_target');
			});
			element.parents('div:eq(1)').after($('<span validateref=""></span>').addClass("jsmart5_validate").text(message));

		} else if (type == 'checkbox') {
			element.closest('div').find('>label').addClass('jsmart5_validate_target');
			element.closest('div').next('span').after($('<span validateref=""></span>').addClass("jsmart5_validate").text(message));

		} else if (type == 'switch') {
			element.closest('div').addClass('jsmart5_validate_target');
			element.closest('div').after($('<span validateref=""></span>').addClass("jsmart5_validate").text(message));

		} else if (type == 'inputgroup') {
			element.parents('div:eq(2)').addClass('jsmart5_validate_target');
			element.parents('div:eq(3)').after($('<span validateref=""></span>').addClass("jsmart5_validate").text(message));

		} else {
			element.addClass('jsmart5_validate_target');
			element.after($('<span validateref=""></span>').addClass("jsmart5_validate").text(message));
		}
	}

	function doExecute(a, exec) {
		var dialogs = [];
		var funcs = [];
		var execute = '';

		if (exec && exec.length > 0) {
			var execs = exec.split(';');
	
			for (var i = 0; i < execs.length; i++) {
				if (execs[i].indexOf(DIALOG_OPEN) >= 0) {
					dialogs.push(execs[i].substring(0, execs[i].indexOf('.')));
	
				} else if (execs[i].indexOf(DIALOG_CLOSE) >= 0) {
					execute += "$('" + getId(execs[i].substring(0, execs[i].indexOf('.'))) + "').dialog('close');";
	
				} else {
					var execFunc = window[execs[i]];

					if (typeof execFunc === 'function') {
						funcs.push(execFunc);
					} else {
						execute += execs[i] + ";";
					}
				}
			}
		}

		for (var i = 0; i < funcs.length; i++) {
			try {
				funcs[i](a);
			} catch(err) {
				showOnConsole(err.message); 
			}
		}

		if (execute.length > 0) {
			try {
				eval(execute);
			} catch(err) {
				showOnConsole(err.message); 
			}
		}
	
		for (var i = 0; i < dialogs.length; i++) {
			openDialog(dialogs[i]);
		}
		return dialogs;
	}

	function doList(async, element) {
		if ($(element) && $(element).attr('ajaxoutside')) {
	
			var options = getBasicAjaxOptions(async);
			var closestForm = $(element).closest('form');
			var postParam = getAjaxOutsideParams(element);
	
			if (closestForm && closestForm.length > 0) {
				if (!doValidate($(closestForm).attr('id'))) {
					return;
				}
			} else {
				postParam = $.param(postParam);			
			}
	
			options.data = postParam;
	
			if (closestForm && closestForm.length > 0) {
				doFormPlaceHolders(closestForm);
				$(closestForm).ajaxSubmit(options);
			} else {
				$.ajax(options);
			}
		}
	}

	function clearTableFilterTimer(id) {
		for (var i = 0; i < TABLE_FILTER_TIMERS.length; i++) {
			if (TABLE_FILTER_TIMERS[i].id == id) {
				clearTimeout(TABLE_FILTER_TIMERS[i].value);
				break;
			}
		}
	}

	function doTable(async, id, element) {
		if ($(element) && $(element).attr('ajax')) {
	
			var ajax = $.parseJSON($(element).attr('ajax'));
			ajax.method = 'post';
	
			var timeout = 0;
			if (ajax.action == 'FILTER') {
				timeout = 1000;
				clearTableFilterTimer(id);
			}

			var timerId = setTimeout(function() {
				var json = getTableAction(ajax);
	
				var options = getAjaxOptions(async, ajax);
				var postParam = [{name: ajax.name, value: json}];
	
				var closestForm = $(getId(id)).closest('form');
				if (closestForm && closestForm.length > 0) {
					if (!doValidate($(closestForm).attr('id'))) {
						return;
					}
				} else {
					postParam = $.param(postParam);
				}
	
				options.data = postParam;
	
				if (closestForm && closestForm.length > 0) {
					doFormPlaceHolders(closestForm);
					$(closestForm).ajaxSubmit(options);
				} else {
					$.ajax(options);
				}
	
			}, timeout);

			TABLE_FILTER_TIMERS.push({id: id, value: timerId});
		}
	}

	function doTableSelect(async, id, element) {
		if ($(element) && $(element).attr('ajaxeval')) {
	
			var ajax = $.parseJSON($(element).attr('ajaxeval'));
			ajax.method = 'post';
	
			// Do not send selection when edition is active
			var editRow = $(element).find('div[inputwrapper]');
			if (editRow.length && editRow.is(":visible")) {
				return;
			}
	
			var json = doTableSelectAction(id, ajax);
	
			var options = getAjaxOptions(async, ajax);
			var postParam = [{name: ajax.name, value: json}];
	
			var closestForm = $(getId(id)).closest('form');
			if (closestForm && closestForm.length > 0) {
				if (!doValidate($(closestForm).attr('id'))) {
					return;
				}
			} else {
				postParam = $.param(postParam);
			}
	
			options.data = postParam;
	
			if (closestForm && closestForm.length > 0) {
				doFormPlaceHolders(closestForm);
				$(closestForm).ajaxSubmit(options);
			} else {
				$.ajax(options);
			}
		}
	}

	function doTableScroll(id) {
		var table = $(getId(id));
		var tbody = $(getId(id) + '>tbody');
	
		tbody.find('tr:first').find('td').each(function (index) {
			$(this).css({'width': $(this).width() + 'px'});
		});
	
		table.css({'display': 'block', 'width': table.width() + 'px'});
		tbody.css({'display': 'block', 'overflow': 'auto', 'width': tbody.width() + 'px', 'height': (tbody.height() - 30) + 'px'});
	
		tbody.scroll(function() {
			if (($(this).scrollTop() + $(this).outerHeight()) >= $(this)[0].scrollHeight) {
	
				var ajax = $.parseJSON($(this).parent().attr('ajaxscroll'));
				ajax.method = 'post';
	
				if (ajax.end && ajax.end == 'false') {
					
					var json = getTableAction(ajax);
					var closestForm = $(getId(id)).closest('form');
					var postParam = [{name: ajax.name, value: json}];
	
					if (!closestForm || closestForm.length == 0) {
						postParam = $.param(postParam);
					}
					
					var options = getAjaxOptions(false, ajax);
					options.data = postParam;
	
					options.success = function(data) {
						var reset = $(data).find(RESET_PATH);
						if (reset && reset.length > 0) {
							$(location).attr('href', $(location).attr('href'));
						} else {
							var currTable = $(getId(id));
							var ajaxTable = $(data).find(getId(id));
							var currTBody = $(getId(id) + '>tbody');
							var ajaxTBody = $(data).find(getId(id) + '>tbody');
	
							currTable.attr('ajaxscroll', ajaxTable.attr('ajaxscroll'));
							currTBody.append(ajaxTBody.find('tr'));
	
							resetMessage(data);
							var redirect = $(data).find(REDIRECT_PATH); 
							if (redirect && redirect.length > 0) {
								$(location).attr('href', redirect.val());
							}
						}
					};
	
					if (closestForm.length > 0) {
						doFormPlaceHolders(closestForm);
						$(closestForm).ajaxSubmit(options);
					} else {
						$.ajax(options);
					}
				}
			}
		});
	}

	function doTableRowExpand(id) {
		var tableElement = $(getId(id));
		tableElement.find("tr:odd").addClass("odd");
		tableElement.find("tr:not(.odd)").hide();
		tableElement.find("tr:first-child").show();
	
		tableElement.find("tr.odd").find("td").click(function(e) {
			var editRow = $(this).find('div[inputwrapper]');
			if (!$(this).hasClass("jsmart5_table_cell_edit_column") && (!editRow.length || !editRow.is(":visible"))) {
				$(this).parent().next("tr").toggle();
			}
	    });
	}

	function doTableEdit(async, id, element) {
		if ($(element) && $(element).attr('ajax')) {
			var ajax = $.parseJSON($(element).attr('ajax'));
			ajax.method = 'post';
	
			var json = {};
			json.edit = ajax.action;
			json.index = ajax.index;
			json.first = ajax.first;
			json.size = ajax.size;
			json.varname = ajax.varname;
	
			var found = false;
			json.values = [];
	
			$(getId(ajax.update)).find('*[name]').each(function(index) {
				if ($(this).attr("name").indexOf(TAG_INIT) >= 0) {
					$(this).removeAttr('enabled');
					$(this).attr('disabled', 'disabled');
					json.values.push({name: $(this).attr("name"), value: $(this).val()});
				}
			});
	
			if (json.values.length == 0) {
				json.values = null;
			}
	
			getTableSortFilter(ajax, json);
	
			var options = getAjaxOptions(async, ajax);
			var postParam = [{name: ajax.name, value: JSON.stringify(json)}];
	
			var closestForm = $(getId(id)).closest('form');
			if (closestForm && closestForm.length > 0) {
				if (!doValidate($(closestForm).attr('id'))) {
					return;
				}
			} else {
				postParam = $.param(postParam);
			}
	
			options.data = postParam;
	
			if (closestForm && closestForm.length > 0) {
				doFormPlaceHolders(closestForm);
				$(closestForm).ajaxSubmit(options);
			} else {
				$.ajax(options);
			}
		}
	}

	function doTableEditStart(id, item, index, first) {
		item.hide();
		$(getId(id + EDIT_CELL_CONFIRM_ITEM_ID + index + "_" + first)).css("display", "inline-block");
		$(getId(id + EDIT_CELL_CANCEL_ITEM_ID + index + "_" + first)).css("display", "inline-block");
	
		item.closest('tr').find("div[outputwrapper]").each(function(index) {
			$(this).hide();
		});
		item.closest('tr').find("div[inputwrapper]").each(function(index) {
			$(this).find("*[disabled]").each(function(index) {
	
				if ($(this).attr("name").indexOf(TAG_INIT) >= 0) {
					$(this).removeAttr('disabled');
					$(this).attr('enabled', 'enabled');
					$(this).css({'width': $(this).closest('td').width() - 30});
				}
			});
			$(this).show();
		});
	}

	function doTableEditCancel(id, item, index, first) {
		item.hide();
		$(getId(id + EDIT_CELL_CONFIRM_ITEM_ID + index + "_" + first)).hide();
		$(getId(id + EDIT_CELL_START_ITEM_ID + index + "_" + first)).show();
	
		item.closest('tr').find("div[outputwrapper]").each(function(index) {
			$(this).show();
		});
		item.closest('tr').find("div[inputwrapper]").each(function(index) {
			$(this).hide();
	
			$(this).find("*[enabled]").each(function(index) {
	
				if ($(this).attr("name").indexOf(TAG_INIT) >= 0) {
					$(this).removeAttr('enabled');
					$(this).attr('disabled', 'disabled');
				}
			});
		});
	}

	function doTab(id) {
		if (id && id.length > 0) {
			var tabElement = $(getId(id));
			var ajaxAttr = tabElement.attr('ajax');
	
			if (ajaxAttr) {
				var ajax = $.parseJSON(ajaxAttr);
	
				tabElement.find('>div').each(function(index) {
					$(this).hide();
				});
	
				var value = null;
				if ($(getId(id + TAB_INDEX))) {
					value = $(getId(id + TAB_INDEX)).val();
				}
	
				var tabItem = null;
				if (value && value.length > 0) {
					tabItem = tabElement.find('>ul li[val="' + value + '"]');
					var closeItem = tabElement.find('>ul li[active="true"]');
	
					if (closeItem.attr('val') != value) {
						if (closeItem) {
							closeItem.removeAttr('active');
							$(getId(closeItem.attr('tab'))).hide();
						}
					}
				} else {
					tabItem = tabElement.find('>ul li:first');
				}
	
				tabItem.attr('active', 'true');
				$(getId(tabItem.attr('tab'))).show();
	
				tabElement.find('>ul li').each(function(index) {
					if (ajax.hover == 'true') {
						$(this).mouseover(function() {
							applyTab($(this), ajax.ajax, id, ajax.collapsible, ajax.position);
						});
					} else {
						$(this).click(function() {
							applyTab($(this), ajax.ajax, id, ajax.collapsible, ajax.position);
						});
					}
				});
			}
		}
	}

	function doSelect(async, id) {
		if (id && id.length > 0) {
			var postParam = [];
			var selectElement = $(getId(id));
			var options = getBasicAjaxOptions(async);
			var closestForm = selectElement.closest('form');
			var elementParam = getElementParam(selectElement, false);
			
			if (closestForm && closestForm.length > 0) {
				if (!doValidate($(closestForm).attr('id'))) {
					return;
				}
				if (elementParam.length > 0 && elementParam[0].value == null) {
					postParam.push({name: elementParam[0].name, value: elementParam[0].value});
				}
			} else {
				for (var i = 0; i < elementParam.length; i++) {
					postParam.push({name: elementParam[i].name, value: elementParam[i].value});
				}
				postParam = $.param(postParam);			
			}
	
			options.data = postParam;
	
			if (closestForm && closestForm.length > 0) {
				doFormPlaceHolders(closestForm);
				$(closestForm).ajaxSubmit(options);
			} else {
				$.ajax(options);
			}
		}
	}

	function doDate(input) {
		var regExp = null;
		var formatInput = $('#' + input.attr('id') + DATE_FORMAT);

		if (formatInput) {
			var format = formatInput.val();
			if (format) {
				var parts = null;

				if (format.indexOf('/') >= 0) {
					parts = format.split('/');

				} else if (format.indexOf('-') >= 0) {
					parts = format.split('-');
				}
				
				if (parts && parts.length > 0) {
					var regex = '/\b%s\b/';

					for (var i = 0; i < parts.length; i++) {
						// It means month literal
						if (parts[i].length > 2 && parts[i].toLowerCase().indexOf('m') >= 0) {
							regex = regex.replace('%s', '[a-zA-Z]*%s');
						} else {
							regex = regex.replace('%s', '\d{1,' + parts[i].length + '}%s');
						}
						regex = (i < parts.length - 1) ? regex.replace('%s', '[\/-]%s') : regex.replace('%s', '');
					}
					regExp = new RegExp(regex);
				}
			}
		}

		if (!regExp) {
			regExp = new RegExp(/\b\d{1,2}[\/-]\d{1,2}[\/-]\d{4}\b/);
		}

		if (!regExp.test(input.val())) {
			input.attr('value', input.attr('dbackup'));
		}
	}

	function doPanel(id) {
		if (id && id.length > 0) {
			var panelCollapse = $(getId(id + PANEL_COLLAPSE));
			if (panelCollapse) {
				var panelContent = $(getId(id + PANEL_CONTENT));
	
				panelCollapse.click(function() {
					var collapse = $(this);
	
					if (panelContent.attr("closed")) {
						panelContent.removeAttr("closed");
	
						panelContent.slideDown("slow", function() {
							// Animation complete.
							collapse.css({'border-bottom-width': '1px'});
	
							var triangle = $(getId(id + PANEL_COLLAPSE) + '>div');
							if (triangle && triangle.length > 0) {
								triangle.css({
									'border-top': '5px solid',
									'border-top-color': triangle.css('borderLeftColor'),
									'border-left': '5px solid transparent',
									'border-bottom': '0px',
									'border-right': '5px solid transparent'
								});
							}
						});
	
					} else {
						panelContent.attr("closed", "true");
	
						panelContent.slideUp("slow", function() {
							// Animation complete.
							collapse.css({'border-bottom-width': '0px'});
	
							var triangle = $(getId(id + PANEL_COLLAPSE) + '>div');
							if (triangle && triangle.length > 0) {
								triangle.css({
									'border-top': '5px solid transparent',
									'border-left': '5px solid',
									'border-left-color': triangle.css('borderTopColor'),
									'border-bottom': '5px solid transparent',
									'border-right': '0px'
								});
							}
						});
					}
				});
	
				if (panelContent.attr("closed")) {
					panelContent.hide();
					panelCollapse.css({'border-bottom-width': '0px'});
				}
			}
		}
	}

	function doCheckbox(input) {
		var value = $(input).val();
		var name = $(input).attr("name");
	
		if (!value || value == 'false') {
			$(input).attr('value', 'true');
			$("input:hidden[name='" + name + "']").each(function(index) {
				$(this).remove();
			});
		} else {
			$(input).attr('value', 'false');
			$(input).after($('<input type="hidden" name="' + name + '" value="false" />'));
		}
	}

	function doGroupItem(async, name, element) {
		if (element && element.length > 0) {
			var postParam = [];
			var options = getBasicAjaxOptions(async);
			var closestForm = $(element).closest('form');
			var elementParam = getElementParam(element, false);
	
			if (closestForm && closestForm.length > 0) {
				if (!doValidate($(closestForm).attr('id'))) {
					return;
				}
				if (elementParam.length > 0 && elementParam[0].value == null) {
					postParam.push({name: elementParam[0].name, value: elementParam[0].value});
				}
			} else {
				for (var i = 0; i < elementParam.length; i++) {
					postParam.push({name: elementParam[i].name, value: elementParam[i].value});
				}
				postParam = $.param(postParam);			
			}
	
			options.data = postParam;
	
			if (closestForm && closestForm.length > 0) {
				doFormPlaceHolders(closestForm);
				$(closestForm).ajaxSubmit(options);
			} else {
				$.ajax(options);
			}
		}
	}

	function doSwitch(id, async, ajax) {
		var switchInput = $(getId(id + SWITCH_INPUT));
		var switchButton = $(getId(id + SWITCH_BUTTON));
		var state = switchInput.val();
	
		if (state && state == 'true') {
			state = 'false';
			switchInput.val(state);
			$(getId(id + SWITCH_SPAN_ON)).animate({width: "0%"}, "fast");
			$(getId(id + SWITCH_SPAN_OFF)).animate({width: "100%", left: "0%"}, "fast");
	
			switchButton.animate({left: "0%"}, "fast", function() {
				if (ajax) {
					doSwitchAction(async, id, state);
				}
			});
		} else {
			state = 'true';
			switchInput.val(state);
			$(getId(id + SWITCH_SPAN_ON)).animate({width: "100%"}, "fast");
			$(getId(id + SWITCH_SPAN_OFF)).animate({width: "0%", left: "100%"}, "fast");
			
			switchButton.animate({left: $(getId(id)).width() - switchButton.outerWidth(true)}, "fast", function() {
				if (ajax) {
					doSwitchAction(async, id, state);
				}
			});
		}
	}
	
	function resetSwitch(id) {
		if ($(getId(id)).is('div[switch="switch"]')) {
			doResetSwitch(id);
		} else {
			$(getId(id)).find('div[switch="switch"]').each(function(index) {
				doResetSwitch($(this).attr("id"));
			});
		}
	}

	function doResetSwitch(id) {
		if (id && id.length > 0) {
			var switchElement = $(getId(id));
			if (switchElement.attr('switch') && !switchElement.attr('resized')) {
				switchElement.attr('resized', 'true');

				doResetLabel(switchElement, switchElement);

				var switchButton = $(getId(id + SWITCH_BUTTON));
				var switchSpanOn = $(getId(id + SWITCH_SPAN_ON));
				var switchSpanOff = $(getId(id + SWITCH_SPAN_OFF));
	
				var spanWidth = switchSpanOn.outerWidth(true);
				if (switchSpanOff.outerWidth(true) > spanWidth) {
					spanWidth = switchSpanOff.outerWidth(true);
				}

				var switchWidth = getInlineStyle(switchElement, 'width');
				if (switchWidth && switchWidth.indexOf('px') >= 0) {
					switchWidth = parseInt(switchWidth.replace('px', ''));
					if (switchWidth > spanWidth) {
						spanWidth = switchWidth;
					}
				}

				switchElement.css({'width': spanWidth + switchButton.outerWidth()});

				var switchHeight = switchElement.height();
				switchButton.height(switchHeight - 4); // 4 is the difference from button to container
				switchSpanOn.outerHeight(switchHeight);
				switchSpanOff.outerHeight(switchHeight);
				
				switchSpanOn.css({'line-height': switchHeight * 0.07});
				switchSpanOff.css({'line-height': switchHeight * 0.07});

				var state = $(getId(id + SWITCH_INPUT)).val();
	
				if (state && state == 'true') {
					switchSpanOn.css({width: "100%"});
					switchSpanOff.css({width: "0%", left: "100%"});
					switchButton.css({left: switchElement.width() - switchButton.outerWidth(true)});
	
				} else {
					switchSpanOn.css({width: "0%"});
					switchSpanOff.css({width: "100%", left: "0%"});
					switchButton.css({left: "0%"});
				}
			}
		}
	}

	function doBalloon(target, position, opened, length, message) {
		if (target && target.length > 0) {
			var targetElement = $(getId(target));
	
			targetElement.hover(function() {
				openBalloon(target, position, length, message);
			},
			function() {
				closeBalloon(target);
			});
	
			if (targetElement.is(":visible") && (opened == 'true' || opened == true)) {
				openBalloon(target, position, length, message);
			}
		}
	}

	function doCarousel(id) {
		if (id && id.length > 0) {
			clearCarouselTimer(id);
	
			var carousel = $(getId(id));
			var slides = carousel.find('.jsmart5_carousel_slides');
	
			var width = carousel.attr('width');
			var height = carousel.attr('height');
			var timer = carousel.attr('timer');
	
			var transType = carousel.attr('transitionType');
			if (!transType || transType.length == 0) {
				transType = 'slide';
			}
			var transTime = carousel.attr('transitionTime');
			if (!transTime || transTime.length == 0) {
				transTime = 600;
			}
	
			carousel.width(width);
			carousel.height(height);
	
			// Capture title size
			if (carousel.find('>p').length > 0) {
				carousel.height(parseInt(height) + carousel.find('>p').outerHeight(true));
			}
	
			slides.width(width);
			slides.height(height);
	
			if (slides.find('[id^="' + id + CAROUSEL_SLIDE + '"]').length > 1) {
	
				slides.find('[id^="' + id + CAROUSEL_SLIDE + '"]').each(function (index) {
					if (index == 0) {
						carousel.attr("current", $(this).attr('id'));
					} else {
						$(this).css({'top': -5000, 'left': width + 'px'});
					}
	
					$(this).find('label').each(function() {
						$(this).css({'top': height - $(this).outerHeight(true), 'left': (width - $(this).outerWidth(true)) / 2});
					});
				});
	
				var timerId = setInterval(function() {doCarouselTransition(id, null, transType, transTime)}, timer);
				CAROUSEL_TIMERS.push({id: id, value: timerId});
				doCarouselControl(id, transType, transTime);
			}
		}
	}

	function doMessage(messages, options) {
		var id = options.id;
		var effectTime = 500;
	
		var autoHide = options.autoHide;
		var width = 200;
		if (options.width) {
			width = options.width;
		}
		var duration = 2000;
		if (options.duration) {
			duration = options.duration;
		}
		var position = 'right top';
		if (options.position) {
			position = options.position;
		}
		var modal = false;
		if (options.modal) {
			modal = options.modal;
		}
		var onShow = null;
		if (options.onShow) {
			onShow = options.onShow;
		}
		var onClose = null;
		if (options.onClose) {
			onClose = options.onClose;
		}
	
		if (id && id.length > 0) {

			// For fixed messages
			var div = $(getId(id));

			if (div && div.length > 0) {
				div.empty().hide();
	
				if (messages.info.length > 0) {
					var divInfo = $('<div fixedmessage="fixed" class="jsmart5_message_info"/>').css({'position': 'relative', 'min-width': width, 'text-align': 'left'}).html($('<span />')).appendTo(div);
					for (var i = 0; i < messages.info.length; i++) {
						divInfo.append($('<a />').text(messages.info[i])).append($('<br />'));
					}
				}
				if (messages.warning.length > 0) {
					var divWarning = $('<div fixedmessage="fixed" class="jsmart5_message_warning"/>').css({'position': 'relative', 'min-width': width, 'text-align': 'left'}).html($('<span />')).appendTo(div);
					for (var i = 0; i < messages.warning.length; i++) {
						divWarning.append($('<a />').text(messages.warning[i])).append($('<br />'));
					}
				}
				if (messages.error.length > 0) {
					var divError = $('<div fixedmessage="fixed" class="jsmart5_message_error"/>').css({'position': 'relative', 'min-width': width, 'text-align': 'left'}).html($('<span />')).appendTo(div);
					for (var i = 0; i < messages.error.length; i++) {
						divError.append($('<a />').text(messages.error[i])).append($('<br />'));
					}
				}
				if (messages.success.length > 0) {
					var divSuccess = $('<div fixedmessage="fixed" class="jsmart5_message_success"/>').css({'position': 'relative', 'min-width': width, 'text-align': 'left'}).html($('<span />')).appendTo(div);
					for (var i = 0; i < messages.success.length; i++) {
						divSuccess.append($('<a />').text(messages.success[i])).append($('<br />'));
					}
				}
	
				if (div.children().length > 0) {
					div.children().each(function(index) {
						if (autoHide) {
							setTimeout((function(child, exec) {
								return function() {
									child.remove();
									if ($('div[fixedmessage="fixed"]').length == 0) {
										if (exec) {
											doExecute(null, exec);
										}
									}
						        };
						    })($(this), onClose), duration);
						} else {
							$(this).css({'cursor': 'pointer'}).click(function(e) {
								$(this).remove();
								if ($('div[fixedmessage="fixed"]').length == 0) {
									if (onClose) { 
										doExecute(null, onClose);
									}
								}
							});
						}
					});
					div.show();
					if (onShow) {
						doExecute(null, onShow);
					}
				}
			}
		} else {

			// For auto messages

			var lastTopOffset = 0;
			$('div[automessage="auto"]').each(function(index) {
				lastTopOffset += $(this).outerHeight(true);
				if ($(this).css('marginTop')) {
					lastTopOffset -= parseInt($(this).css('marginTop').replace('px', ''));
				}
			});

			var divs = [];
			var totalHeight = 0;

			for (var i = 0; i < messages.info.length; i++) {
				divs[divs.length] = $('<div automessage="auto" position="' + position + '" class="jsmart5_message_info"/>')
					.css({'opacity': 0, 'min-width': width}).html($('<span />').after($('<a />').text(messages.info[i]))).appendTo('body');
				totalHeight += parseInt(divs[divs.length - 1].outerHeight(true));
			}
			for (var i = 0; i < messages.warning.length; i++) {
				divs[divs.length] = $('<div automessage="auto" position="' + position + '" class="jsmart5_message_warning"/>')
					.css({'opacity': 0, 'min-width': width}).html($('<span />').after($('<a />').text(messages.warning[i]))).appendTo('body');
				totalHeight += parseInt(divs[divs.length - 1].outerHeight(true));
			}
			for (var i = 0; i < messages.error.length; i++) {
				divs[divs.length] = $('<div automessage="auto" position="' + position + '" class="jsmart5_message_error"/>')
					.css({'opacity': 0, 'min-width': width}).html($('<span />').after($('<a />').text(messages.error[i]))).appendTo('body');
				totalHeight += parseInt(divs[divs.length - 1].outerHeight(true));
			}
			for (var i = 0; i < messages.success.length; i++) {
				divs[divs.length] = $('<div automessage="auto" position="' + position + '" class="jsmart5_message_success"/>')
					.css({'opacity': 0, 'min-width': width}).html($('<span />').after($('<a />').text(messages.success[i]))).appendTo('body');
				totalHeight += parseInt(divs[divs.length - 1].outerHeight(true));
			}
		
			if (divs.length > 0) {
				var divWidth = parseInt(divs[0].outerWidth(true));
				var divHeight = parseInt(divs[0].outerHeight(true));
				var divMarginTop = 0;
				if (divs[0].css('marginTop')) {
					divMarginTop = parseInt(divs[0].css('marginTop').replace('px', ''));
				}

				// Width of the Brower
				var docWidth = parseInt($(window).width());
				var docHeight = parseInt($(window).height());
		
				// Scroll Position
				var scrollTop = 0; // Not used for fixed position
				var scrollLeft = 0; //Not used for fixed position

				var overallTop = 0;
				var overallLeft = 0;
				var positions = position.split(" ");

				// Horizontal position
				if (positions.length > 0) {
					if (positions[0] == 'left') {
						overallLeft = scrollLeft;
					} else if (positions[0] == 'center') {
						overallLeft = scrollLeft + ((docWidth - divWidth) / 2);
					} else if (positions[0] == 'right') {
						overallLeft = scrollLeft + docWidth - divWidth;
					}
				}
		
				// Vertical position
				if (positions.length > 1) {
					if (positions[1] == 'top') {
						overallTop = scrollTop + lastTopOffset;
					} else if (positions[1] == 'center') {
						overallTop = scrollTop + lastTopOffset + ((docHeight - totalHeight) / 2);
					} else if (positions[1] == 'bottom') {
						overallTop = scrollTop + docHeight - divHeight - lastTopOffset;
					}
				} else if (positions[0] == 'center') {
					overallTop = scrollTop + lastTopOffset + ((docHeight - totalHeight) / 2);
				}
		
				if (modal) {
					var overlay = $('<div class="jsmart5_overlay_message" />').appendTo('body').show();
				}

				for (var i = 0; i < divs.length; i++) {
					if (i > 0) {
						onShow = null;
						if (positions.length > 1 && positions[1] == 'bottom') {
							overallTop -= divs[i - 1].outerHeight(true) - divMarginTop;
						} else {
							overallTop += divs[i - 1].outerHeight(true) - divMarginTop;
						}
					}

					showMessage(divs[i], overallLeft, overallTop, effectTime, onShow);

					if (autoHide) {
						closeMessage(divs[i], modal, duration, effectTime, onClose);
					} else {
						$(divs[i]).css({'cursor': 'pointer'}).click(function(e) {
							closeMessage($(this), modal, 0, effectTime, onClose);
						});
					}
				}
			}
		}
	}
	
	function doButtonDropDown(element) {
		if (element) {
			var dropDown = $(element).closest('div.jsmart5_button_group').find('>ul');
			var button = $(element).closest('div.jsmart5_button_group').find('>button, >input');

			$(dropDown).css({'position': 'absolute'});
			$(dropDown).css({'left': $(button).position().left, 'top': $(button).position().top + $(button).outerHeight(true)});
			$(dropDown).show();

			if ($(element).attr('dropdown')) {
				$(dropDown).hide();
				$(element).removeAttr('dropdown');
			} else {
				$(element).attr('dropdown', 'opened');

				$(dropDown).find('li').click(function() {
					$(dropDown).hide();
					$(element).removeAttr('dropdown');
				});
			}
		}
	}

	function doLinkDropDown(element) {
		if (element) {
			var dropDown = $(element).closest('div.jsmart5_link_group').find('>ul');
			var link = $(element).closest('div.jsmart5_link_group').find('>a.jsmart5_link');

			$(dropDown).css({'position': 'absolute'});
			$(dropDown).css({'left': $(link).position().left, 'top': $(link).position().top + $(link).outerHeight(true)});
			$(dropDown).show();

			if ($(element).attr('dropdown')) {
				$(dropDown).hide();
				$(element).removeAttr('dropdown');
			} else {
				$(element).attr('dropdown', 'opened');

				$(dropDown).find('li').click(function() {
					$(dropDown).hide();
					$(element).removeAttr('dropdown');
				});
			}
		}
	}

	function resetMenu(id) {
		if ($(getId(id)).is('ul[menu="menu"]')) {
			doMenu(id);
		} else {
			$(getId(id)).find('ul[menu="menu"]').each(function(index) {
				doMenu($(this).attr("id"));
			});
		}
	}
	
	function applyMenu(menu, level) {
		menu.find('>li').each(function() {
			$(this).find('>ul').each(function() {
				$(this).attr('level', level);
				$(this).hide();
				applyMenu($(this), level + 1);
			})
		});
	}

	function doMenu(id) {
		if (id && id.length > 0) {
			var menu = $(getId(id));
			var type = menu.attr('type');
	
			menu.attr('level', 'root');
			applyMenu(menu, 1);
	
			menu.find('li').mouseenter(function () {
				var subMenu = $(this).find('>ul');
				if (subMenu && subMenu.length > 0) {

					subMenu.css({'position':'absolute'});

					if (type == 'top') {
						if (parseInt(subMenu.attr('level')) != 1) {
							subMenu.css({'left': $(this).position().left + $(this).outerWidth(true), 'top': $(this).position().top});
						} else {
							subMenu.css({'left': $(this).position().left, 'top': $(this).position().top + $(this).outerHeight(true)});
						}
	
					} else if (type == 'left') {
						subMenu.css({'left': $(this).position().left + $(this).outerWidth(true), 'top': $(this).position().top});
	
					} else if (type == 'right') {
						subMenu.css({'left': $(this).position().left - subMenu.outerWidth(true), 'top': $(this).position().top});
	
					} else if (type == 'bottom') {
						if (parseInt(subMenu.attr('level')) != 1) {
							subMenu.css({'left': $(this).position().left + $(this).outerWidth(true), 
										 'top': $(this).position().top - subMenu.outerHeight(true) + $(this).outerHeight(true)});
						} else {
							subMenu.css({'left': $(this).position().left, 'top': $(this).position().top - subMenu.outerHeight(true)});
						}
					}
	
					subMenu.show();
				}
			})
			.mouseleave(function () {
				var subMenu = $(this).find('>ul');
				if (subMenu && subMenu.length > 0) {
					if (subMenu.children(":focus").length == 0) {
						subMenu.hide();
				    }
				}
			});
		}
	}

	function resetTree(id) {
		if ($(getId(id)).is('ul[tree="tree"]')) {
			doTree(id);
		} else {
			$(getId(id)).find('ul[tree="tree"]').each(function(index) {
				doTree($(this).attr("id"));
			});
		}
	}

	function doTree(id) {
		if (id && id.length > 0) {
			var tree = $(getId(id));
			
			tree.find('ul').each(function(index) {
				$(this).hide();
			});

			tree.find('li').click(function(index) {
				var triangle = $(this).find('div:not([class="jsmart5_tree_item_mark_empty"])').first();

				var ul = $(this).find('ul').first();
				if (ul) {
					if (ul.attr('opened')) {
						if (triangle) {
							triangle.removeClass('jsmart5_tree_item_mark_opened');
							triangle.addClass('jsmart5_tree_item_mark_closed');
						}
						ul.removeAttr('opened');
						ul.hide();					
					} else {
						if (triangle) {
							triangle.removeClass('jsmart5_tree_item_mark_closed');
							triangle.addClass('jsmart5_tree_item_mark_opened');
						}
						ul.attr('opened', 'true');
						ul.show();						
					}
				}
				return false;
			});
		}
	}

	function doProgress(id) {
		if (id && id.length > 0) {
			var progressFrame = $(getId(id + PROGRESS_FRAME));
			clearProgress(id);
			applyProgress(id, true);
	
			var ajax = $.parseJSON(progressFrame.attr('ajax'));
	
			if (ajax.interval && ajax.interval > 0) {
				var intervalId = setInterval(applyProgress, ajax.interval, id, false);
				PROGRESS_TIMERS.push({id: id, value: intervalId});
			}
		}
	}

	function doRange(id) {
		applyRange(id, true); 
		var rangeFrame = $(getId(id + RANGE_FRAME));

		if (!rangeFrame.attr("disabled") || rangeFrame.attr("disabled") != "disabled") {
			rangeFrame.css({'cursor': 'pointer'});

			rangeFrame.mousedown(function(event) {
				var paddingLeft = 0;
				var rangeBar = rangeFrame.find('>div');
				var rangeValue = $(getId(id + RANGE_VALUE));
				var width = rangeFrame.width();
				var offset = rangeFrame.parent().offset();
				var ajax = $.parseJSON($(this).attr('ajax'));
				var step = null;

				if (ajax.step && ajax.step.length > 0) {
					step = ((width * parseInt(ajax.step)) / parseInt(ajax.max - ajax.min));
				}

				if (rangeFrame.css('paddingLeft')) {
					paddingLeft = parseInt(rangeFrame.css('paddingLeft').replace('px', ''));
				}

		        $(window).mousemove(function(event) {
		        	var minX = 0;
					var posX = event.pageX - offset.left - paddingLeft;

					if (step) {
						posX |= 0;
						posX -= posX % step;
					}

					if (posX > width) {
						posX = width;
					} else if (posX < 0) {
						posX = 0;
					}

					rangeBar.css({left: posX});

					if (rangeValue && rangeValue.length > 0) {
						var percent = (posX - minX) / (width - minX);
						var value = ((percent * parseInt(ajax.max - ajax.min)) | 0) + parseInt(ajax.min);
						value = value > ajax.max ? ajax.max : value < ajax.min ? ajax.min : value;
						rangeValue.text(value);
					}
		        });
		    })
		    .mouseup(function(event) {
		        $(window).unbind("mousemove");

		        var minX = 0;
	        	var width = $(this).width();
				var offset = $(this).parent().offset();
				var posX = event.pageX - offset.left;

				var ajax = $.parseJSON($(this).attr('ajax'));
				var input = $(this).find('input:hidden');

				if ($(this).css('paddingLeft')) {
					posX -= parseInt($(this).css('paddingLeft').replace('px', ''));
				}

				if (ajax.step && ajax.step.length > 0) {
					posX |= 0;
					posX -= posX % ((width * parseInt(ajax.step)) / parseInt(ajax.max - ajax.min));
				}

				if (posX > width) {
					posX = width;
				} else if (posX < 0) {
					posX = 0;
				}

				var percent = (posX - minX) / (width - minX);
				var value = ((percent * parseInt(ajax.max - ajax.min)) | 0) + parseInt(ajax.min);
				value = value > ajax.max ? ajax.max : value < ajax.min ? ajax.min : value;
				input.val(value);

				applyRange(id, false);
			});
		}
	}

	function openDialog(id) {
		$(getId(id)).dialog('open');
		resetMasks(id);
		resetPlaceHolders(id);
		resetTable(id);
		resetSelects(id);
		doListenEnterKey(id);
		resetBalloon(id);
		resetKeypad(id);
	}
		
	function closeDialog(id) {
		$(getId(id)).dialog('close');
	}

	function getRestJsonBody(element, bodyRoot) {
		var json = '';
		var closestForm = $(element).closest('form');

		if (closestForm && closestForm.length > 0) {
			if (!doValidate($(closestForm).attr('id'))) {
				return;
			}

			var restGroups = $(closestForm).find('div[type="restarray"]');

			if (restGroups && restGroups.length > 0) {
				var restMap = {};
				var initGroup = false;

				restGroups.each(function(index) {

					var groupName = $(this).attr('rest');
					if (!groupName || groupName.length == 0) {
						groupName = 'none';
					} else {
						initGroup = true;
					}

					if (!restMap[groupName]) {
						restMap[groupName] = '';
					}
					restMap[groupName] += getRestJsonItem($(this)) + ',';
				});

				if (bodyRoot && bodyRoot.length > 0) {
					if (initGroup) {
						json = '{\"' + bodyRoot + '\":{';
					} else {
						json = '{\"' + bodyRoot + '\":[';
					}
				} else {
					if (initGroup) {
						json = '{';
					} else {
						json = '[';
					}
				}

				for (var groupName in restMap) {
					var item = restMap[groupName];
					if (groupName != 'none') {
						json += '\"' + groupName + '\":[' + item.substring(0, item.length - 1) + '],';
					} else {
						json += item.substring(0, item.length - 1) + ',';
					}
				}
				
				if (json.length > 1) {
					json = json.substring(0, json.length - 1);
				}

				if (bodyRoot && bodyRoot.length > 0) {
					if (initGroup) {
						json += '}}';
					} else {
						json += ']}';
					}
				} else {
					if (initGroup) {
						json += '}';
					} else {
						json += ']';
					}
				}
			} else {
				if (bodyRoot && bodyRoot.length > 0) {
					json += '{\"' + bodyRoot + '\":';
				}

				json = getRestJsonItem(closestForm);
				
				if (bodyRoot && bodyRoot.length > 0) {
					json += '}';
				}
			}
		}
		return json;
	}

	function getRestJsonItem(parent) {
		var json = '{';
		var checkgroups = new Array();

		$(parent).find('*[rest]').each(function(index) {

			var elementParam = getElementParam($(this), true);
			if (elementParam.length > 0) {

				var name = $(this).attr('name');
				if (contains(checkgroups, name)) {
					return;
				}
				checkgroups[checkgroups.length] = name;

				var rest = $(this).attr('rest');

				if (elementParam[0].array == true) {
					json += '\"' + rest + '\":[';
					if (elementParam[0].value && elementParam[0].value.length > 0) {
						var values = elementParam[0].value.split(',');
						for (var i = 0; i < values.length; i++) {
							json += '\"' + values[i] + '\",';
						}
						json = json.substring(0, json.length - 1);
					}
					json += '],';
				} else {
					if (elementParam[0].value || elementParam[0].value == false) {
						json += '\"' + rest + '\":\"' + elementParam[0].value + '\",';
					} else {
						json += '\"' + rest + '\":null,';
					}
				}
			}
		});

		if (json.length > 1) {
			json = json.substring(0, json.length - 1);
		}

		return json + '}';
	}

	function getRestXmlBody(element, bodyRoot) {
		var xml = '<?xml version="1.0" encoding="UTF-8" ?>';
		
		if (bodyRoot && bodyRoot.length > 0) {
			xml += '<' + bodyRoot + '>';
		} else {
			xml += '<root>';
		}

		var closestForm = $(element).closest('form');

		if (closestForm && closestForm.length > 0) {
			if (!doValidate($(closestForm).attr('id'))) {
				return;
			}

			var restGroups = $(closestForm).find('div[type="restarray"]');

			if (restGroups && restGroups.length > 0) {
				restGroups.each(function(index) {
					var groupName = $(this).attr('rest');
					if (!groupName || groupName.length == 0) {
						groupName = 'none';
					}
					xml += '<' + groupName + '>' + getRestXmlItem($(this)) + '</' + groupName + '>';
				});
			} else {
				xml += getRestXmlItem(closestForm);
			}
		}

		if (bodyRoot && bodyRoot.length > 0) {
			xml += '</' + bodyRoot + '>';
		} else {
			xml += '</root>';
		}
		return xml;
	}

	function getRestXmlItem(parent) {
		var xml = '';
		var checkgroups = new Array();

		$(parent).find('*[rest]').each(function(index) {

			var elementParam = getElementParam($(this), true);
			if (elementParam.length > 0) {
				
				var name = $(this).attr('name');
				if (contains(checkgroups, name)) {
					return;
				}
				checkgroups[checkgroups.length] = name;

				var rest = $(this).attr('rest');

				if (elementParam[0].array == true) {
					if (elementParam[0].value && elementParam[0].value.length > 0) {
						var values = elementParam[0].value.split(',');
						for (var i = 0; i < values.length; i++) {
							xml += '<' + rest + '>' + values[i] + '</' + rest + '>';
						}
					}
				} else {
					if (elementParam[0].value || elementParam[0].value == false) {
						xml += '<' + rest + '>' + elementParam[0].value + '</' + rest + '>';
					} else {
						xml += '<' + rest + ' />';
					}
				}
			}
		});

		return xml;
	}

	function getRestQueryParams(element) {
		var queryParams = '';
		var ajax = $.parseJSON($(element).attr('ajax'));
		if (ajax.params) {
			for (var i = 0; i < ajax.params.length; i++) {
				queryParams += ajax.params[i].name + '=' + ajax.params[i].value + '&';
			}
		}
		return queryParams.length > 0 ? queryParams.substring(0, queryParams.length - 1) : queryParams;
	}
	
	function getBasicAjaxOptions(async) {
		return {
			type: 'post', 
			url: $(location).attr('href') + ($(location).attr('href').indexOf('?') >= 0 ? '&' : '?') + new Date().getTime(),
			success: function (data) {
				var reset = $(data).find(RESET_PATH); 
				if (reset && reset.length > 0) {
					$(location).attr('href', $(location).attr('href'));
				} else {
					resetMessage(data);
					var redirect = $(data).find(REDIRECT_PATH); 
					if (redirect && redirect.length > 0) {
						$(location).attr('href', redirect.val());
					}
				}
			},
			error: function (xhr, status, error) {
				showOnConsole(xhr.responseText);
			},
			complete: function (xhr, status) {
				jQuery.event.trigger('ajaxStop');
			},
			async: async
		};
	}
	
	function getAjaxOptions(async, ajax) {
		return {
			type: ajax.method, 
			url: $(location).attr('href') + ($(location).attr('href').indexOf('?') >= 0 ? '&' : '?') + new Date().getTime(),
			beforeSend: function (data) {
				doExecute(data, ajax.before);
			},
			success: function (data) {
				var reset = $(data).find(RESET_PATH); 
				if (reset && reset.length > 0) {
					$(location).attr('href', $(location).attr('href'));
				} else {
					if (ajax.url && ajax.url.length > 0) {
						$(location).attr('href', ajax.url);
					} else {
						doUpdate(data, ajax.update);
						var dialogs = doExecute(data, ajax.exec);
						doReset(ajax.update, dialogs);
	
						resetMessage(data);
						var redirect = $(data).find(REDIRECT_PATH); 
						if (redirect && redirect.length > 0) {
							$(location).attr('href', redirect.val());
						}
					}
				}
			},
			error: function (xhr, status, error) {
				showOnConsole(xhr.responseText);
			},
			complete: function (xhr, status) {
				jQuery.event.trigger('ajaxStop');
			},
			async: async
		};
	}
	
	function getAjaxParams(element) {
		var params = [];
		if ($(element) && $(element).attr('ajax')) {
			var paramAjax = $.parseJSON($(element).attr('ajax'));
			
			if (paramAjax.action) {
				params.push({name: paramAjax.action, value: 0});
			}
	
			if (paramAjax.params) {
				for (var i = 0; i < paramAjax.params.length; i++) {
					params.push({name: paramAjax.params[i].name, value: paramAjax.params[i].value});
				}
			}
		}
		return params;
	}
	
	function getAjaxOutsideParams(element) {
		var params = [];
		if ($(element) && $(element).attr('ajaxoutside')) {
			var ajaxParams = $.parseJSON($(element).attr('ajaxoutside'));
			if (ajaxParams.params.length > 0) {
				params = ajaxParams.params;
			}
		}
		return params;
	}
	
	//This is only works for ajax tag inside table tag
	function getAjaxEvalParams(element) {
		var params = [];
		if ($(element) && $(element).attr('ajaxeval')) {
			var ajaxEval = $.parseJSON($(element).attr('ajaxeval'));
			var json = doTableSelectAction(ajaxEval.id, ajaxEval);
			params = [{name: ajaxEval.name, value: json}];
		}
		return params;
	}
	
	function getElementParam(element, rest) {
		var elementParam = [];
		var name = $(element).attr('name');
		var checkgroups = new Array();
	
		if ($(element).is('select') && $(element).attr('multiple')) {
			var values = $(element).val();
			if (values && values.length > 0) {
				if (rest) {
					var value = "";
					for (var i = 0; i < values.length; i++) {
						value += values[i] + ",";
					}
					if (value.length > 0) {
						elementParam.push({name: name, value: value.substring(0, value.length - 1), array: true});
					}
				} else {
					for (var i = 0; i < values.length; i++) {
						elementParam.push({name: name, value: values[i], array: true});
					}
				}
			}
	
			if (elementParam.length == 0) {
				elementParam.push({name: name, value: null, array: true});
			}
		} else if ($(element).is('input') && $(element).attr('checkgroup')) {
			if (!contains(checkgroups, name)) {
				checkgroups[checkgroups.length] = name;
	
				var values = [];
				$(element).parents('div:eq(1)').find("input:checked[name='" + name + "']").each(function(index) {
					values.push($(this).val());
				});
	
				if (rest) {
					var value = "";
					for (var i = 0; i < values.length; i++) {
						value += values[i] + ",";
					}
					if (value.length > 0) {
						elementParam.push({name: name, value: value.substring(0, value.length - 1), array: true});
					}
				} else {
					for (var i = 0; i < values.length; i++) {
						elementParam.push({name: name, value: values[i], array: true});
					}
				}
	
				if (elementParam.length == 0) {
					elementParam.push({name: name, value: null, array: true});
				}
			}
		} else if ($(element).is('input') && $(element).attr('radiogroup')) {
			if (!contains(checkgroups, name)) {
				checkgroups[checkgroups.length] = name;
	
				var val = $(element).parents('div:eq(1)').find("input:checked[name='" + name + "']").val();
				elementParam.push({name: name, value: val, array: false});
			}
		} else if ($(element).is('input:checkbox')) {
			var value = $(element).val();
	
			if (!value || value == 'false') {
				elementParam.push({name: name, value: false, array: false});
			} else {
				elementParam.push({name: name, value: true, array: false});
			}
		} else {
			elementParam.push({name: name, value: $(element).val(), array: false});
		}
	
		return elementParam;
	}
	
	function doUpdate(a, update) {
		if (update && update.length > 0) {
			var updateEval = "";
			var updates = update.split(',');
			for (var i = 0; i < updates.length; i++) {
				if (!doTableScrollUpdate(a, updates[i])) {
					var updateId = getId(updates[i]);
					updateEval += "$('" + updateId + "').replaceWith($(a).find('" + updateId + "'));";
				}
			}
			if (updateEval.length > 0) {
				try {
					eval(updateEval);
				} catch(err) {
					showOnConsole(err.message); 
				}
			}
		}
	}
	
	function doReset(update, dialogs) {
		if (update && update.length > 0) {
			var updates = update.split(',');
	
			for (var i = 0; i < updates.length; i++) {
				var id = updates[i];
	
				if (!contains(dialogs, id)) {
					resetMasks(id);
					resetDatePicker(id);
					resetPlaceHolders(id);
					resetTable(id);
					resetSelects(id);
					resetBalloon(id);
					resetCarousel(id);
					resetPanel(id);
					resetKeypad(id);
					resetTab(id);
					resetCaptcha(id);
					resetSwitch(id);
					resetProgress(id);
					resetRange(id);
					resetMenu(id);
					resetTree(id);
					resetAutocomplete(id);
					resetSearch(id);
				}
			}
		}
	}
	
	function getTableAction(ajax) {
		var json = {};
		json.action = ajax.action;
		json.first = ajax.first;
		getTableSortFilter(ajax, json);
		return JSON.stringify(json);
	}
	
	function getTableSortFilter(ajax, json) {
		if (ajax.sort) {
			var sorts = ajax.sort.split(',');
			if (sorts.length > 1) {
				json.sort = {name: sorts[0], order: sorts[1]};
			} else {
				json.sort = null;
			}
		} else {
			json.sort = null;
		}
	
		if (ajax.filter) {
			var filters = ajax.filter.split(',');
	
			if (filters.length > 1) {
				json.filters = [];
				var filterItem = null;
	
				for (var i = 0; i < filters.length; i++) {
					if (i % 2 == 0) {
						filterItem = {};
						filterItem.name = filters[i];
					} else {
						var input = $(getId(filters[i]));
						filterItem.field = filters[i];
	
						if (input.val() != input.attr('placeholder')) {
							filterItem.value = input.val();
						} else {
							filterItem.value = null;
						}
						json.filters.push(filterItem);
					}
				}
			} else {
				json.filters = null;
			}
		} else {
			json.filters = null;
		}
	}
	
	function doTableSelectAction(id, ajax) {
		var json = {};
		var tableElement = $(getId(id));
	
		json.select = ajax.action;
		json.first = ajax.first;
		json.size = ajax.size;
		json.type = 'SINGLE';
	
		if (ajax.multi == 'true') {
			json.type = 'MULTI';
		}
	
		if (ajax.multi == 'true' && ajax.indexes == 'all') {
			var checked = $(getId(id + MULTI_SELECT_ALL)).attr('checked');
			if (!checked) {
				ajax.indexes = "";
			} else {
				var indexes = '';
				tableElement.find('input[id]').each(function(index) {
					var input = $(this).attr('id');
					if (input && input.length > 0 && input.indexOf(MULTI_SELECT_ITEM_ID) >= 0) {
						indexes += input.replace(id + MULTI_SELECT_ITEM_ID, '') + ',';
					}
				});
				ajax.indexes = indexes.substring(0, indexes.length -1);
			}
		}
	
		// Eliminate duplicated values for muli select case
		var idxs = ajax.indexes.split(",");
		for (var i = 0; i < idxs.length; i++) {
			var check = ajax.indexes.indexOf(idxs[i]);
			if (check >= 0 && ajax.indexes.indexOf(idxs[i], check + 1) >= 0) {
				idxs.pop(idxs[i]);
				idxs.splice(i, 1);
			}
		}
	
		json.indexes = [];
		for (var i = 0; i < idxs.length; i++) {
			json.indexes.push(idxs[i]);
		}
	
		var prevIdxs = tableElement.attr('indexes').split(',');
		if (compare(idxs, prevIdxs) === false) {
			if (idxs.length == 0) {
				json.expand = 'false';
			} else {
				json.expand = 'true';
			}
		} else {
			var prevExpand = tableElement.attr('expandstatus');
			if (prevExpand && prevExpand == 'false') {
				json.expand = 'true';
			} else {
				json.expand = 'false';
			}
		}
	
		getTableSortFilter(ajax, json);
	
		return JSON.stringify(json);
	}
	
	function doTableScrollUpdate(a, update) {
		var found = false;
	
		// For select row case the tag is related to scrollable table
		var ajaxTr = eval("$(a).find('tr[select=\"" + update + "\"]')");
	
		if (ajaxTr.length > 0 && ajaxTr.closest('table[ajaxScroll]').length > 0) {
			var tdWidths = [];
			var currTr = $('tr[select="' + update + '"]');
	
			currTr.find('td').each(function (index) {
				tdWidths[index] = $(this).width(); 
			});
	
			ajaxTr.find('td').each(function (index) {
				$(this).css({'width': tdWidths[index]});
			});
			
			currTr.replaceWith(ajaxTr);
			found = true;
	
		} else {
			// For edit rows case the tag is related to scrollable table
			ajaxTr = eval("$(a).find('tr[id=\"" + update + "\"]')");
			
			if (ajaxTr.length > 0 && ajaxTr.closest('table[ajaxScroll]').length > 0) {
	
				var tdWidths = [];
				var currTr = $('tr[id="' + update + '"]');
	
				currTr.find('td').each(function (index) {
					tdWidths[index] = $(this).width(); 
				});
	
				ajaxTr.find('td').each(function (index) {
					$(this).css({'width': tdWidths[index]});
				});
				
				currTr.replaceWith(ajaxTr);
				found = true;
	
			} else {
				// For scrollabe table update case filter or sort is placed in table tag
				var ajaxTable = eval("$(a).find('" + getId(update) + ">table[ajaxScroll]')");
	
				if (ajaxTable.length > 0) {
					var currTable = $(getId(update) + ">table[ajaxScroll]");
	
					currTable.attr('ajaxscroll', ajaxTable.attr('ajaxscroll'));
	
					var tdWidths = [];
					var ajaxTBody = ajaxTable.find('>tbody');
					var currTHead = currTable.find('>thead');
					var currTBody = currTable.find('>tbody');
	
					currTHead.find('th').each(function (index) {
						tdWidths[index] = $(this).outerWidth();
					});
	
					currTHead.replaceWith(ajaxTable.find('>thead'));
	
					if (ajaxTBody.find('tr:first').find('td').length > 1) {
						ajaxTBody.find('tr:first').find('td').each(function (index) {
							$(this).css({'width': tdWidths[index] + 'px'});
						});
					} else {
						ajaxTBody.find('tr:first').find('td').css({'width': currTBody.width() + 'px', 'height': currTBody.height() + 'px'});
					}
	
					currTBody.find('tr').remove();
					currTBody.append(ajaxTBody.find('tr'));
	
					found = true;
				}
			}
		} 
		return found;
	}
	
	function doRowToggle(tableId, selectedIndex, expandStatus) {
		doTableRowExpand(tableId);
	
		if (selectedIndex.length > 0) {
			var tableElement = $(getId(tableId));
			var rowLength = tableElement.find("tr:odd").length;
			tableElement.find("tr:odd").each(function(index) {
		
				for (var i = 0; i < selectedIndex.length; i++) {
					if (selectedIndex[i] % rowLength == index) {
						if (expandStatus.indexOf('true') >= 0) {
							$(this).next("tr").toggle();
						}
						break;
					}
				}
			});
		}
	}
	
	function resetCaptcha(id) {
		if (id && id.length > 0) {
			var captchaElement = $(getId(id));
			if (captchaElement.is('input[captcha]')) {
				try {
					eval(captchaElement.attr('captcha'));
				} catch(err) {
					showOnConsole(err.message);
				}
			} else {
				captchaElement.find('input[captcha]').each(function(index) {
					try {
						eval($(this).attr('captcha'));
					} catch(err) {
						showOnConsole(err.message);
					}
				});
			}
		}
	}
	
	function resetTab(id) {
		if (id && id.length > 0) {
			var tabElement = $(getId(id));
			if (tabElement.is('div[tab="tab"]')) {
				this.doTab(id);
			} else {
				tabElement.find('div[tab="tab"]').each(function(index) {
					doTab($(this).attr('id'));
				});
			}
		}
	}
	
	function applyTab(tabItem, ajax, id, collapsible, position) {
		var input = $(getId(id + TAB_INDEX));
	
		if (tabItem.attr('active') && collapsible == 'true') {
			tabItem.removeAttr('active');
			$(getId(tabItem.attr('tab'))).hide();
	
			if (input && input.length > 0) {
				input.val(null);
				if (ajax == 'true') {
					doTabAction(true, id, input.attr('name'), input.val());
				}
			}
		} else {
			var closeItem = $(getId(id)).find('>ul li[active="true"]');
			if (closeItem) {
				closeItem.removeAttr('active');
				$(getId(closeItem.attr('tab'))).hide();
			}
	
			tabItem.attr('active', 'true');
			$(getId(tabItem.attr('tab'))).show();
	
			if (input && input.length > 0) {
				input.val(tabItem.attr('val'));
				if (ajax == 'true') {
					doTabAction(true, id, input.attr('name'), input.val());
				}
			}
		}
	}
	
	function doTabAction(async, id, name, value) {
		var options = getBasicAjaxOptions(async);
		var closestForm = $(getId(id)).closest('form');
		var postParam = [{name: name, value: value}];
	
		if (closestForm && closestForm.length > 0) {
			if (!doValidate($(closestForm).attr('id'))) {
				return;
			}
		} else {
			postParam = $.param(postParam);			
		}
	
		options.data = postParam;
	
		if (closestForm && closestForm.length > 0) {
			doFormPlaceHolders(closestForm);
			$(closestForm).ajaxSubmit(options);
		} else {
			$.ajax(options);
		}
	}
	
	function resetMasks(id) {
		if (id && id.length > 0) {
			var maskElement = $(getId(id));
			if (maskElement.is('input') && maskElement.attr('mask')) {
				maskElement.unmask();
				maskElement.mask(maskElement.attr('mask'));
			} else {
				maskElement.find('*[mask]').each(function(index) {
					$(this).unmask();
					$(this).mask($(this).attr('mask'));
				});
			}
		}
	}
	
	function resetDatePicker(id) {
		if (id && id.length > 0) {
			var datepicker = $(getId(id));
			if (datepicker.is('*[date]')) {
				try {
					if (datepicker.next('button')) {
						datepicker.next('button').remove();
					}
					eval(datepicker.attr('date'));
				} catch(err) {
					showOnConsole(err.message);
				}
			} else {
				datepicker.find('*[date]').each(function(index) {
					try {
						if ($(this).next('button')) {
							$(this).next('button').remove();
						}
						eval($(this).attr('date'));
					} catch(err) {
						showOnConsole(err.message);
					}
				});
			}
		}
	}
	
	function resetSelects(id) {
		if (id && id.length > 0) {
			var selectElement = $(getId(id));
			if (selectElement.is('select')) {
				selectElement.trigger('update');
			} else {
				selectElement.find('select').each(function(index) {
					$(this).trigger('update');
				});
			}
		}
	}
	
	function resetTable(id) {
		if (id && id.length > 0) {
			var tableElement = $(getId(id)); 
			if (tableElement.is('table')) {
				if (tableElement.attr('expandtable')) {
					var tableId = tableElement.attr('id');
					var selIndexes = tableElement.attr('indexes').split(',');
					var expandStatus = tableElement.attr('expandstatus');
					doRowToggle(tableId, selIndexes, expandStatus);
				}
	
				if (tableElement.attr('indexes')) {
					applyResetTable(tableElement);
				}
	
			} else {
				tableElement.find('table[expandtable]').each(function(index) {
					var tableId = $(this).attr('id');
					var selIndexes = $(this).attr('indexes').split(',');
					var expandStatus = $(this).attr('expandstatus');
					doRowToggle(tableId, selIndexes, expandStatus);
				});
	
				tableElement.find('table[indexes]').each(function(index) {
					applyResetTable($(this));
				});
			}
		}
	}
	
	function applyResetTable(table) {
		if (table && table.length > 0) {
			var totalInputs = 0;
			var tableId = table.attr('id');
			var selIndexes = table.attr('indexes').split(',');
		
			table.find('input[id]').each(function(index) {
				var inputId = $(this).attr('id');
		
				if (inputId && inputId.length > 0 && inputId.indexOf(MULTI_SELECT_ITEM_ID) >= 0) {
					totalInputs++;
					var listIndex = inputId.replace(tableId + MULTI_SELECT_ITEM_ID, '');
		
					if (contains(selIndexes, listIndex)) {
						$(this).attr('checked', 'checked');
					} else {
						$(this).removeAttr('checked');
					}
				}
			});
		
			if (totalInputs == selIndexes.length) {
				$(getId(tableId + MULTI_SELECT_ALL)).attr('checked', 'checked');
			}
		}
	}
	
	function initPlaceHolders() {
		$('*[placeholder]').each(function(index) {
			var input = $(this);
		});
	
		$('*[placeholder]').focus(function() {
			var input = $(this);
			if (input.val() == input.attr('placeholder')) {
				input.val('');
			    input.removeClass('placeholder');
			}
			return false;
	
		}).blur(function() {
			var input = $(this);
			if (input.val() == '' || input.val() == input.attr('placeholder')) {
			    input.addClass('placeholder');
			    input.val(input.attr('placeholder'));
			}
		}).blur();
	
		$('*[placeholder]').closest('form').submit(function() {
			doFormPlaceHolders(this);
		});
	}
	
	function resetPlaceHolders(id) {
		if (id && id.length > 0) {
			var placeHolder = $(getId(id));
			placeHolder.find('*[placeholder]').each(function(index) {
				var input = $(this);
			});
	
			placeHolder.find('*[placeholder]').focus(function() {
				var input = $(this);
				if (input.val() == input.attr('placeholder')) {
					input.val('');
				    input.removeClass('placeholder');
				}
				return false;
	
			}).blur(function() {
				var input = $(this);
				if (input.val() == '' || input.val() == input.attr('placeholder')) {
				    input.addClass('placeholder');
				    input.val(input.attr('placeholder'));
				}
			}).blur();
	
			placeHolder.find('*[placeholder]').closest('form').submit(function() {
				doFormPlaceHolders(this);
			});
		}
	}
	
	function doFormPlaceHolders(form) {
		if (form && form.length > 0) {
			$(form).find('*[placeholder]').each(function() {
				var input = $(this);
			    if (input.val() == input.attr('placeholder')) {
			    	input.val('');
			    }
			});
		}
	}
	
	function doListenEnterKey(id) {
		if (id && id.length > 0) {
			var listenEnter = $(getId(id));
			listenEnter.unbind('keypress');
			listenEnter.bind('keypress', function(e) {
				if (e.keyCode == 13) {
					var button = listenEnter.dialog('option', 'listenEnter');
					if (button && button.length > 0) {
						$(getId(button)).click();
						return false;
					}
				}
		    });
		}
	}
	
	function resetPanel(id) {
		if ($(getId(id)).is('div[type="panel"]')) {
			doPanel(id);
		} else {
			$(getId(id)).find('div[type="panel"]').each(function(index) {
				doPanel($(this).attr("id"));
			});
		}
	}
	
	function initCheckbox() {
		$('input:checkbox').each(function(index) {
			var value = $(this).val();
			var name = $(this).attr("name");
			if (!value || value == 'false') {
				$(this).after($('<input type="hidden" name="' + name + '" value="false" />'));
			}
		});
	}

	function doSwitchAction(async, id, state) {
		var options = getBasicAjaxOptions(async);
		var switchInput = $(getId(id + SWITCH_INPUT));
		var postParam = [{name: switchInput.attr("name"), value: state}];
		var closestForm = switchInput.closest('form');
	
		if (closestForm && closestForm.length > 0) {
			if (!doValidate($(closestForm).attr('id'))) {
				return;
			}
		} else {
			postParam = $.param(postParam);			
		}
	
		options.data = postParam;
	
		if (closestForm && closestForm.length > 0) {
			doFormPlaceHolders(closestForm);
			$(closestForm).ajaxSubmit(options);
		} else {
			$.ajax(options);
		}
	}
	
	function resetKeypad(id) {
		if (id && id.length > 0) {
			var keypad = $(getId(id));
			if (keypad.is('input') && $(getId(id + KEYPAD_HOLDER))) {
				try {
					eval($(getId(id + KEYPAD_HOLDER)).val());
				} catch(err) {
					showOnConsole(err.message);
				}
			} else {
				keypad.find('input[keypad="keypad"]').each(function(index) {
					try {
						eval($(this).val());
					} catch(err) {
						showOnConsole(err.message);
					}
				});
			}
		}
	}
	
	function resetBalloon(id) {
		var foundBalloon = false;
		$(getId(id)).find('span[type="balloon"]').each(function(index) {
			doBalloon($(this).attr("target"), $(this).attr("position"), $(this).attr("opened"),
					$(this).attr("length"), $(this).attr("message"));
			foundBalloon = true;
		});
		if (!foundBalloon) {
			$(getId(id + BALLOON_HOLDER)).each(function(index) {
				doBalloon($(this).attr("target"), $(this).attr("position"), $(this).attr("opened"),
						$(this).attr("length"), $(this).attr("message"));
			});
		}
	}
	
	function openBalloon(target, position, length, message) {
	    var spikeLength = 10;
	    var spikeWidth = 8;
	    var spikeSkew = 0;
	    
	    var canvasMargin = 1;
	    var canvasX = canvasMargin;
	    var canvasY = canvasMargin;
	
	    var targetElement = $(getId(target)); 
	    var targetWidth = targetElement.outerWidth();
	    var targetHeight = targetElement.outerHeight();
	    var targetPosition = targetElement.position();
	
	    // Get values from css
	    var styleElement = $(getId(target + BALLOON_HOLDER));
	
	    var padding = $(styleElement).css('paddingTop');
	    var radius = $(styleElement).css('borderTopLeftRadius');
	    var strokeWidth = $(styleElement).css('borderTopWidth');
	    var strokeColor = $(styleElement).css('borderTopColor');
	    var fillColor = $(styleElement).css('backgroundColor');
	    var fontColor = $(styleElement).css('color');
	
	    if (!padding || padding.length == 0) {
	    	padding = 10;
	    } else {
	    	padding = parseInt(padding.replace('px', ''));
	    }
	
	    if (!radius || radius.length == 0) {
	    	radius = 4;
	    } else {
	    	radius = parseInt(radius.replace('px', ''));
	    }
	
	    if (!strokeWidth || strokeWidth.length == 0) {
	    	strokeWidth = 1;
	    } else {
	    	strokeWidth = parseInt(strokeWidth.replace('px', ''));
	    }
	
	    if (!fillColor || fillColor.length == 0) {
	    	fillColor = '#ffff66';
	    }
	    if (!strokeColor || strokeColor.length == 0) {
	    	strokeColor = '#000000';
	    }
	
	    // Remove old balloon if present
	    var oldBalloon = $(getId(target + BALLOON_WRAPPER));
	    if (oldBalloon && oldBalloon.length > 0) {
	    	oldBalloon.remove();
	    }
	
	    var parent = targetElement.parent();
	    var text = $('<div></div>').css({'padding': padding, 'position': 'absolute', 'color': fontColor}).text(message);
	    var box = $('<div id="' + target + BALLOON_WRAPPER + '"></div>').css({'position': 'absolute'}).append(text).appendTo(parent);
	
	    var canvas = document.createElement('canvas');
	    var context = canvas.getContext("2d");
	    
	    // Set dimensions and position for content
	    var metrics = context.measureText(message);

	    if (metrics.width < length) {
	    	length = metrics.width + (padding * 4);
	    }
	    text.css({'width': length});

	    if (position == 'right') {
	    	canvasX = canvasMargin + spikeLength;
	    	text.css({'left': canvasX});
	    }
	    if (position == 'bottom') {
	    	canvasY = canvasMargin + spikeLength;
	    	text.css({'top': canvasY});
	    }
	
	    // Set dimensions for div wrapper
	    if (position == 'right' || position == 'left') {
	    	box.css({'width': text.outerWidth() + spikeLength + (canvasMargin * 2), 'height': text.outerHeight() + (canvasMargin * 2)});
	    } else {
	    	box.css({'width': text.outerWidth() + (canvasMargin * 2), 'height': text.outerHeight() + spikeLength + (canvasMargin * 2)});
	    }
	
	    // Positionate canvas
	    $(canvas).attr('width', box.outerWidth()).attr('height', box.outerHeight()).appendTo(box);
	
	    // Positionate div wrapper
	    if (position == 'bottom') {
	    	box.css({'top': targetPosition.top + targetHeight, 'left': targetPosition.left + ((targetWidth - box.outerWidth()) / 2)});
	    } else if (position == 'top') {
	    	box.css({'top': targetPosition.top - box.outerHeight(), 'left': targetPosition.left + ((targetWidth - box.outerWidth()) / 2)});
	    } else if (position == 'left') {
	    	box.css({'top': targetPosition.top + ((targetHeight - box.outerHeight()) / 2), 'left': targetPosition.left - box.outerWidth()});
	    } else if (position == 'right') {
	    	box.css({'top': targetPosition.top + ((targetHeight - box.outerHeight()) / 2), 'left': targetPosition.left + targetWidth});
	    }
	
	    drawBalloon(position, context, canvasX, canvasY, text.outerWidth(), text.outerHeight(), 
	    			radius, spikeLength, spikeWidth, spikeSkew, strokeWidth, strokeColor, fillColor);
	}
	
	function closeBalloon(id) {
		$(getId(id + BALLOON_WRAPPER)).each(function(index) {
			$(this).remove();
		});
	}
	
	function drawBalloon(position, ctx, x, y, width, height, radius, spikeLen, spikeW, spikeSkew, strokeW, strokeColor, fillColor) {
		ctx.beginPath();
		ctx.moveTo(x + radius, y);
		
		var skewFactor = spikeSkew > 0 ? (spikeW * 2) : 0;
	
		if (position == 'bottom') { 
			ctx.lineTo(((x + width + skewFactor) / 2) - (spikeW / 2), y);
			ctx.lineTo(((x + width + skewFactor) / 2) + spikeSkew,    y - spikeLen);
			ctx.lineTo(((x + width + skewFactor) / 2) + (spikeW / 2), y);
		}
	
		ctx.lineTo(x + width - radius, y);
		ctx.quadraticCurveTo(x + width, y, x + width, y + radius);
	
		if (position == 'left') {
			ctx.lineTo(x + width,            ((y + height + skewFactor) / 2) - (spikeW / 2));
			ctx.lineTo(x + width + spikeLen, ((y + height + skewFactor) / 2) - spikeSkew);
			ctx.lineTo(x + width,            ((y + height + skewFactor) / 2) + (spikeW / 2));
		}
		
		ctx.lineTo(x + width, y + height - radius);
		ctx.quadraticCurveTo(x + width, y + height, x + width - radius, y + height);
	
		if (position == 'top') {
			ctx.lineTo(((x + width + skewFactor) / 2) + (spikeW / 2), y + height);
			ctx.lineTo(((x + width + skewFactor) / 2) - spikeSkew,    y + height + spikeLen);
			ctx.lineTo(((x + width + skewFactor) / 2) - (spikeW / 2), y + height);
		}
	
		ctx.lineTo(x + radius, y + height);
		ctx.quadraticCurveTo(x, y + height, x, y + height - radius);
	
		if (position == 'right') {
			ctx.lineTo(x,            ((y + height + skewFactor) / 2) - (spikeW / 2));
			ctx.lineTo(x - spikeLen, ((y + height + skewFactor) / 2) - spikeSkew);
			ctx.lineTo(x,            ((y + height + skewFactor) / 2) + (spikeW / 2));
		}
	
		ctx.lineTo(x, y + radius);
		ctx.quadraticCurveTo(x, y, x + radius, y);
		ctx.closePath();
	
		ctx.lineWidth = strokeW;
		ctx.strokeStyle = strokeColor;
		ctx.stroke();
	
		ctx.fillStyle = fillColor;
		ctx.fill();
	}
	
	function resetCarousel(id) {
		if ($(getId(id)).is('div[carousel="carousel"]')) {
			doCarousel(id);
		} else {
			$(getId(id)).find('div[carousel="carousel"]').each(function(index) {
				doCarousel($(this).attr("id"));
			});
		}
	}
	
	function clearCarouselTimer(id) {
		for (var i = 0; i < CAROUSEL_TIMERS.length; i++) {
			if (CAROUSEL_TIMERS[i].id == id) {
				clearInterval(CAROUSEL_TIMERS[i].value);
			}
		}
	}
	
	function doCarouselControl(id, transType, transTime) {
		var carousel = $(getId(id));
		var control = carousel.find('.jsmart5_carousel_control');
		if (control && control.length > 0) {
			var current = carousel.attr("current");
			var currentIndex = parseInt(current.replace(id + CAROUSEL_SLIDE, ''));
			
			var controlWrapper = control.find('>div');
			var totalWidth = 0;
			controlWrapper.find('span').each(function(index) {
				totalWidth += $(this).outerWidth(true);
	
				if (index == currentIndex - 1) {
					$(this).css({'backgroundColor': '#000000', 'color': '#ffffff', 'opacity': 0.4});
				}
				
				$(this).click(function() {
					clearCarouselTimer(id);
					doCarouselTransition(id, $(this).text(), transType, transTime);
				});
			});
			controlWrapper.width(totalWidth);
		}
	
		var arrows = carousel.find('.jsmart5_carousel_control_arrow');
		if (arrows && arrows.length > 0) {
			var width = carousel.attr('width');
			var height = carousel.attr('height');
			
			arrows.find('span').each(function(index) {
				if ($(this).attr('direction') == 'next') {
					$(this).css({'top': (height - $(this).height()) / 2, 'left': width - $(this).width()});
				} else {
					$(this).css({'top': (height - $(this).height()) / 2});
				}
	
				// Just to center the arrow inside span
				var arrowWidth = $(this).width();
				var arrowNext = $(this).attr('direction') == 'next';
	
				$(this).find('>div').each(function() {
					var thisWidth = parseInt($(this).css('borderBottomWidth').replace('px', ''));
					if (arrowNext) {
						$(this).css({'margin-left': (arrowWidth - thisWidth) / 2});
					} else {
						$(this).css({'margin-left': (arrowWidth / 2) - thisWidth});
					}
				});
	
				$(this).hover(function() {
					$(this).stop().fadeTo("fast", 0.7);
				}, function() {
					$(this).stop().fadeTo("fast", 0.0);
				});
				
				$(this).click(function() {
					var current = carousel.attr("current");
					var currentIndex = parseInt(current.replace(id + CAROUSEL_SLIDE, ''));
					if ($(this).attr('direction') == 'next') {
						currentIndex++;
					} else {
						currentIndex--;	
					}
					clearCarouselTimer(id);
					doCarouselTransition(id, currentIndex.toString(), transType, transTime);
				});
			});
		}
	}
	
	function doCarouselTransition(id, nextIdx, transType, transTime) {
		var carousel = $(getId(id));
		var width = carousel.attr('width');
		var current = carousel.attr("current");
		var nextIndex = parseInt(current.replace(id + CAROUSEL_SLIDE, '')) + 1;
	
		if (nextIdx) {
			if (nextIdx == '0') {
				var last = carousel.find('[id^="' + id + CAROUSEL_SLIDE + '"]').last();
				nextIndex = parseInt(last.attr('id').replace(id + CAROUSEL_SLIDE, ''));
			} else {
				nextIndex = parseInt(nextIdx);
			}
		}
	
		var next = $(getId(id + CAROUSEL_SLIDE + nextIndex));
		if (!next || next.length == 0) {
			nextIndex = 1;
			next = $(getId(id + CAROUSEL_SLIDE + nextIndex));
		}
		carousel.attr("current", next.attr("id"));
	
		var control = carousel.find('.jsmart5_carousel_control');
		if (control && control.length > 0) {
			control.find('span').each(function(index){
				if (index == nextIndex - 1) {
					$(this).css({'backgroundColor': '#000000', 'color': '#ffffff', 'opacity': 0.4});
				} else {
					$(this).css({'backgroundColor': '#ffffff', 'color': '#000000', 'opacity': 0.7});
				}
			});
		}
	
		var currentElement = $(getId(current));
	
		if (transType == 'slide') {
			currentElement.stop().animate(
				{"left": (width) * -1 + "px"},
				{duration: transTime, complete: function() {
					currentElement.css({'top': -5000, 'left': width + 'px'});
				}
		    });
		
			next.css({'top': 0, 'left': width + 'px'});
			next.stop().animate({
		        "left": "0px"
		    }, {
		        duration: transTime
		    });
			
		} else if (transType == 'fade') {
			currentElement.css({'zIndex': 2});
			next.css({'top': 0, 'left': 0, 'zIndex': 1});
	
			currentElement.stop().animate(
				{"opacity": 0},
				{duration: transTime, complete: function() {
					currentElement.css({'top': -5000, 'left': 0, 'opacity': 1});
				}
		    });
		}
	}
	
	function resetMessage(data) {
		var message = null;
		$(data).find('input[id^="' + MESSAGES_EXEC + '"]').each(function(index) {
			if ($(this).attr('id').split(MESSAGES_EXEC).length > 1) {
				try {
					eval($(this).val());
				} catch (err) {
					showOnConsole(err.message);
				}
			} else {
				message = $(this);
			}
		});
	
		if (message) {
			try {
				eval($(message).val());
			} catch (err) {
				showOnConsole(err.message);
			}
		}
	}

	function showMessage(div, left, top, effectTime, onShow) {
		$(div).css({'left': left, 'top': top});
		$(div).animate({'opacity': 1}, effectTime, function() {
			if (onShow) {
				if ($('div[automessage="auto"]').length == 1) {
					doExecute(null, onShow);
				}
			}
		});
	}

	function closeMessage(div, modal, duration, effectTime, onClose) {
		setTimeout(function() {
			$(div).animate({'opacity': 0}, effectTime, function() {
				
				var position = $(this).attr('position');
				var scrollTop = parseInt($(window).scrollTop());
				var top = $(this).offset().top - scrollTop;
				var height = $(this).outerHeight(true);

				$(this).remove();

				moveMessages(position, top, height);

				if ($('div[automessage="auto"]').length == 0) {
					if (modal && $(".jsmart5_overlay_message").length > 0) {
						$(".jsmart5_overlay_message").remove();
					}
					if (onClose) { 
						doExecute(null, onClose);
					}
				}
			});
		}, duration);
	}

	function moveMessages(position, top, height) {
		$('div[automessage="auto"]').each(function(index) {

			var divTop = null;
			var scrollTop = parseInt($(window).scrollTop());
			var currTop = $(this).offset().top - scrollTop;

			if (position.indexOf('bottom') >= 0) {
				if (currTop < top) {
					divTop = currTop + $(this).outerHeight();
				}
			} else {
				if (currTop > top) {
					divTop = currTop - height;
				}
			}

			if (divTop || divTop === 0) {
				$(this).animate({"top": divTop + "px"}, "fast");
			}
		});
	}
	
	function resetProgress(id) {
		if ($(getId(id)).is('div[progress="progress"]')) {
			doProgress(id);
		} else {
			$(getId(id)).find('div[progress="progress"]').each(function(index) {
				doProgress($(this).attr("id"));
			});
		}
	}
	
	function clearProgress(id) {
		for (var i = 0; i < PROGRESS_TIMERS.length; i++) {
			if (PROGRESS_TIMERS[i].id == id) {
				clearInterval(PROGRESS_TIMERS[i].value);
			}
		}
	}
	
	function applyProgress(id, init) {
		if (id && id.length > 0) {
			var progressFrame = $(getId(id + PROGRESS_FRAME));
			var progressPercent = $(getId(id + PROGRESS_PERCENT));

			if (init) {
				var totalWidth = $(getId(id)).width();
				var totalHeight = $(getId(id)).height();

				if (progressPercent && progressPercent.length > 0) {
					progressFrame.outerWidth(totalWidth - progressPercent.outerWidth(true) - 50); // 50 for percent value change
				} else {
					progressFrame.outerWidth(totalWidth);
				}

				progressFrame.outerHeight(totalHeight);

				if (progressPercent && progressPercent.length > 0) {
					progressPercent.outerHeight(totalHeight);
				}
			}
	
			var input = $(progressFrame).find('>input:hidden');
			var ajax = $.parseJSON($(progressFrame).attr('ajax'));
	
			if (!input.val()) {
				input.val(ajax.min);
			}

			if (ajax.callback && ajax.callback.length > 0) {
				try {
					var callbackFunc = window[ajax.callback];
					if (typeof callbackFunc === 'function') {
						callbackFunc(input, parseInt(ajax.max), parseInt(ajax.min));
					} else {
						showOnConsole(ajax.callback + ' was not found as a function!');
					}
				} catch(err) {
					showOnConsole(err.message); 
				}
			}

			if (ajax.ajax == 'true' && init == false && ajax.interval && ajax.interval > 0) {
				doProgressAction(id, true);
			}
	
			var value = parseInt(input.val());
			var percentage = ((100 * (value - ajax.min) / (ajax.max - ajax.min)) | 0);
	
			$(progressFrame).find('>div').css({'width': percentage + '%'});

			if (progressPercent && progressPercent.length > 0) {
				progressPercent.text(percentage + '%');
			}
	
			if (value >= parseInt(ajax.max) && ajax.complete && ajax.complete.length > 0) {
				try {
					var completeFunc = window[ajax.complete];
					if (typeof completeFunc === 'function') {
						completeFunc(input);
					}
				} catch(err) {
					showOnConsole(err.message); 
				}
			}
	
			if (value >= ajax.max && ajax.interval && ajax.interval > 0) {
				clearProgress(id);
			}
		}
	}
	
	function doProgressAction(id, update) {
		var options = getBasicAjaxOptions(false);
		var input = $(getId(id + PROGRESS_INPUT));
		var postParam = [{name: input.attr("name"), value: input.val()}];
	
		if (update == true) {
			options.success = function (data) {
				var reset = $(data).find(RESET_PATH); 
				if (reset && reset.length > 0) {
					$(location).attr('href', $(location).attr('href'));
				} else {
					var newInput = $(data).find(getId(id + PROGRESS_INPUT));
					if (newInput && newInput.length > 0) {
						input.replaceWith(newInput);
					}
	
					resetMessage(data);
					var redirect = $(data).find(REDIRECT_PATH); 
					if (redirect && redirect.length > 0) {
						$(location).attr('href', redirect.val());
					}
				}
			};
		}
	
		var closestForm = $(getId(id + PROGRESS_INPUT)).closest('form');
	
		if (closestForm && closestForm.length > 0) {
			if (!doValidate($(closestForm).attr('id'))) {
				return;
			}
		} else {
			postParam = $.param(postParam);			
		}
	
		options.data = postParam;
	
		if (closestForm && closestForm.length > 0) {
			doFormPlaceHolders(closestForm);
			$(closestForm).ajaxSubmit(options);
		} else {
			$.ajax(options);
		}
	}
	
	function resetRange(id) {
		if ($(getId(id)).is('div[range="range"]')) {
			doRange(id);
		} else {
			$(getId(id)).find('div[range="range"]').each(function(index) {
				doRange($(this).attr("id"));
			});
		}
	}
	
	function applyRange(id, init) {
		if (id && id.length > 0) {

			var rangeFrame = $(getId(id + RANGE_FRAME));
			var rangeValue = $(getId(id + RANGE_VALUE));
			var rangeBar = $(rangeFrame).find('>div');
			var rangeTrail = $(rangeFrame).find('>span');

			if (init) {
				var totalWidth = $(getId(id)).width();
				var totalHeight = $(getId(id)).height();

				rangeFrame.outerHeight(totalHeight);

				if (rangeValue && rangeValue.length > 0) {
					rangeFrame.outerWidth(totalWidth - rangeValue.outerWidth(true) - 20); // 20 space for value change
				} else {
					rangeFrame.outerWidth(totalWidth);
				}

				if (rangeValue && rangeValue.length > 0) {
					rangeValue.outerHeight(totalHeight);
				}

				rangeTrail.css({marginTop: ((rangeFrame.height() - rangeTrail.height()) / 2)});
			}

			var input = $(rangeFrame).find('>input:hidden');
			var ajax = $.parseJSON($(rangeFrame).attr('ajax'));
			var minX = 0;
			var width = $(rangeFrame).width();

			if (!input.val()) {
				input.val(ajax.min);
			}

			// Call callback on client
			if (ajax.callback && ajax.callback.length > 0) {
				try {
					var callbackFunc = window[ajax.callback];
					if (typeof callbackFunc === 'function') {
						if (ajax.step && ajax.step.length > 0) {
							callbackFunc(input, parseInt(ajax.max), parseInt(ajax.min), parseInt(ajax.step));
						} else {
							callbackFunc(input, parseInt(ajax.max), parseInt(ajax.min));
						}
					} else {
						showOnConsole(ajax.callback + ' was not found as a function!');
					}
				} catch(err) {
					showOnConsole(err.message); 
				}
			}

			// Call server via ajax
			if (ajax.ajax == 'true' && init == false) {
				doRangeAction(id, false);
			}

			var value = parseInt(input.val());
			var percent = (value - ajax.min) / (ajax.max - ajax.min);			
			var posX = (percent * (width - minX)) + minX;

			rangeBar.css({left: posX});

			if (rangeValue && rangeValue.length > 0) {
				rangeValue.text(value);
			}
		}
	}
	
	function doRangeAction(id, update) {
		var options = getBasicAjaxOptions(true);
		var input = $(getId(id + RANGE_INPUT));
		var postParam = [{name: input.attr("name"), value: input.val()}];
	
		if (update == true) {
			options.success = function (data) {
				var reset = $(data).find(RESET_PATH); 
				if (reset && reset.length > 0) {
					$(location).attr('href', $(location).attr('href'));
				} else {
					var newInput = $(data).find(getId(id + RANGE_INPUT));
					if (newInput && newInput.length > 0) {
						input.replaceWith(newInput);
					}
	
					resetMessage(data);
					var redirect = $(data).find(REDIRECT_PATH); 
					if (redirect && redirect.length > 0) {
						$(location).attr('href', redirect.val());
					}
				}
			};
		}
	
		var closestForm = $(getId(id + PROGRESS_INPUT)).closest('form');
	
		if (closestForm && closestForm.length > 0) {
			if (!doValidate($(closestForm).attr('id'))) {
				return;
			}
		} else {
			postParam = $.param(postParam);			
		}
	
		options.data = postParam;
	
		if (closestForm && closestForm.length > 0) {
			doFormPlaceHolders(closestForm);
			$(closestForm).ajaxSubmit(options);
		} else {
			$.ajax(options);
		}
	}

	function clearAutoCompleteTimer(id) {
		for (var i = 0; i < AUTOCOMPLETE_TIMERS.length; i++) {
			if (AUTOCOMPLETE_TIMERS[i].id == id) {
				clearTimeout(AUTOCOMPLETE_TIMERS[i].value);
			}
		}
	}

	function clearAutoCompleteChar(id, val) {
		for (var i = 0; i < AUTOCOMPLETE_CHARS.length; i++) {
			if (AUTOCOMPLETE_CHARS[i].id == id) {
				AUTOCOMPLETE_CHARS[i].value = val;
				break;
			}
		}
	}

	function pushAutoCompleteChar(id, val) {
		var found = false;
		for (var i = 0; i < AUTOCOMPLETE_CHARS.length; i++) {
			if (AUTOCOMPLETE_CHARS[i].id == id) {
				AUTOCOMPLETE_CHARS[i].value += val;
				found = true;
				break;
			}
		}
		if (!found) {
			AUTOCOMPLETE_CHARS.push({id: id, value: val});
		}
	}

	function getAutoCompleteChar(id) {
		for (var i = 0; i < AUTOCOMPLETE_CHARS.length; i++) {
			if (AUTOCOMPLETE_CHARS[i].id == id) {
				return AUTOCOMPLETE_CHARS[i].value;
			}
		}
		return '';
	}

	function resetAutocomplete(id) {
		if ($(getId(id)).is('input[autocomplete="autocomplete"]')) {
			doResetAutocomplete(id);
		} else {
			$(getId(id)).find('input[autocomplete="autocomplete"]').each(function(index) {
				doResetAutocomplete($(this).attr("id"));
			});
		}
	}

	function doResetAutocomplete(id) {
		var element = $(getId(id));
		var divGroup = element.closest('div.jsmart5_auto_complete_group');
		divGroup.height(element.outerHeight());

		doResetLabel(element, divGroup);

		divGroup.focusin(function() {
			$(this).addClass('jsmart5_auto_complete_group_focus');
		}).focusout(function() {
			$(this).removeClass('jsmart5_auto_complete_group_focus');
		});
	}

	function doAutoComplete(async, id, event) {
		var element = $(getId(id));
		if (element && element.attr('ajax')) {

			clearAutoCompleteTimer(id);
			
			var previous = $(getId(id + AUTOCOMPLETE_VALUES));
			if (previous && previous.length > 0) {
				previous.remove();
			}

			// If delete or backspace, just clear the cache
			if (event.keyCode == 46 || event.keyCode == 8) {
				clearAutoCompleteChar(id, element.val());
				return;
			}

			// If space just return 
			if (event.keyCode == 32) {
				return;
			}
			
			var ajax = $.parseJSON($(element).attr('ajax'));
			var search = element.val().replace(getAutoCompleteChar(id), '');

			if (search.length < ajax.minLength) {
				return;
			}

			var timerId = setTimeout(function() {
				element.addClass('jsmart5_auto_complete_load');

				ajax.method = 'post';
				var options = getAjaxOptions(async, ajax);

				search = element.val().replace(getAutoCompleteChar(id), '');
				var postParam = [{name: ajax.name, value: search}, {name: element.attr('name'), value: element.val()}];

				var closestForm = element.closest('form');
				if (closestForm && closestForm.length > 0) {
					if (!doValidate($(closestForm).attr('id'))) {
						return;
					}
				} else {
					postParam = $.param(postParam);
				}

				options.data = postParam;

				options.success = function(data) {
					var reset = $(data).find(RESET_PATH);
					if (reset && reset.length > 0) {
						$(location).attr('href', $(location).attr('href'));
					} else {
						var div = $(data).find(getId(id + AUTOCOMPLETE_VALUES));

						if (div && div.length > 0) {
							var itemsLength = $(div).find('>ul li').length;
							var divGroup = element.closest('div.jsmart5_auto_complete_group');

							if (itemsLength > 0) {
								divGroup.after($(div));

								var itemHeight = divGroup.outerHeight();
								$(div).outerWidth(divGroup.outerWidth(true));

								var totalHeight = itemHeight * itemsLength;
								if (itemsLength > 10) {
									totalHeight = itemHeight * 10;
								}

								$(div).outerHeight(totalHeight);
								$(div).find('>ul').height($(div).height());
								$(div).find('>ul li').each(function (index) {
									$(this).height(itemHeight);
									$(this).css({'lineHeight': itemHeight + 'px'});

									$(this).click(function() {
										if (ajax.multiple == 'true') {
											pushAutoCompleteChar(id, $(this).text() + ' ');
											element.val(getAutoCompleteChar(id));
										} else {
											element.val($(this).text());
										}
										$(div).remove();
										
										doAutoCompleteCallback(ajax, element, $(this).text());
									});
								});

								$(window).click(function() {
									$(div).remove();
								});

								$(div).css({'left': divGroup.position().left, 'top': divGroup.position().top + itemHeight});
								$(div).show();
							}
						}

						resetMessage(data);
						var redirect = $(data).find(REDIRECT_PATH); 
						if (redirect && redirect.length > 0) {
							$(location).attr('href', redirect.val());
						}
					}
				};
				
				options.complete = function (xhr, status) {
					jQuery.event.trigger('ajaxStop');
					element.removeClass('jsmart5_auto_complete_load');
				};

				if (closestForm && closestForm.length > 0) {
					doFormPlaceHolders(closestForm);
					$(closestForm).ajaxSubmit(options);
				} else {
					$.ajax(options);
				}

			}, 1000);
			
			AUTOCOMPLETE_TIMERS.push({id: id, value: timerId});
		}
	}

	function doAutoCompleteCallback(ajax, input, value) {
		// Call callback on client
		if (ajax.callback && ajax.callback.length > 0) {
			try {
				var callbackFunc = window[ajax.callback];
				if (typeof callbackFunc === 'function') {
					callbackFunc(input, value);
				} else {
					showOnConsole(ajax.callback + ' was not found as a function!');
				}
			} catch(err) {
				showOnConsole(err.message); 
			}
		}
	}
	
	function doResetLabel(element, parent) {
		var inputGroup = element.closest('div.jsmart5_input_group');
		if (inputGroup) {
			inputGroup.height(parent.outerHeight());
		}

		var label = inputGroup.find('>span');
		if (label) {
			var padding = 2;
			var border = 2;
			if (label.css('paddingTop')) {
				padding = parseInt(label.css('paddingTop').replace('px', '')) * 2;
			}
			if (label.css('borderWidth')) {
				border = parseInt(label.css('borderWidth').replace('px', '')) * 2;
			}
			label.height(parent.outerHeight() - padding - border);
			label.css({'lineHeight': (parent.outerHeight() - padding - border) + 'px'});
		}
		
		var button = inputGroup.find('>button');
		if (button) {
			var padding = 2;
			var border = 2;
			if (button.css('paddingTop')) {
				padding = parseInt(button.css('paddingTop').replace('px', '')) * 2;
			}
			if (button.css('borderWidth')) {
				border = parseInt(button.css('borderWidth').replace('px', '')) * 2;
			}
			button.height(parent.outerHeight() - padding - border);
		}
	}
	
	function resetSearch(id) {
		if ($(getId(id)).is('input[search="search"]')) {
			doResetSearch(id);
		} else {
			$(getId(id)).find('input[search="search"]').each(function(index) {
				doResetSearch($(this).attr("id"));
			});
		}
	}
	
	function doResetSearch(id) {
		var element = $(getId(id));
		var divGroup = element.closest('div[class^="jsmart5_search_group"]');
		element.attr('defaultWidth', element.width());
		divGroup.height(element.outerHeight());

		doResetLabel(element, divGroup);
		
		divGroup.focusin(function() {
			$(this).addClass('jsmart5_search_group_focus');
		}).focusout(function() {
			$(this).removeClass('jsmart5_search_group_focus');
		});
	}
	
	function doSearch(async, id, event) {
		var element = $(getId(id));

		if (element && element.attr('ajax') && event && (event.keyCode == 0 || event.keyCode == 13)) {

			var search = element.val();
			if ($.trim(search).length <= 0) {
				return false;
			}
			
			var minWidth = 40;
			if (element.css('minWidth')) {
				var minW = parseInt(element.css('minWidth').replace('px', ''));
				if (minW > 0) {
					minWidth = minW;
				}
			}

			var ajax = $.parseJSON($(element).attr('ajax'));

			// Find previous searches to avoid repeated ones
			if (ajax.track) {

				var repeated = false;
				var divGroup = element.closest('div[class^="jsmart5_search_group"]');
	
				divGroup.find('div.jsmart5_search_close').each(function() {
					var text = $(this).find('>span').text();
					if ($.trim(text) == $.trim(search)) {
						repeated = true;
					}
				});
				
				if (repeated) {
					element.val('');
					return false;
				}
			}

			ajax.method = 'post';
			var options = getAjaxOptions(async, ajax);
			var postParam = getAjaxParams(element);

			var closestForm = element.closest('form');
			if (closestForm && closestForm.length > 0) {
				if (!doValidate($(closestForm).attr('id'))) {
					return false;
				}
			} else {
				postParam = $.param(postParam);
			}
			options.data = postParam;

			options.complete = function (xhr, status) {
				jQuery.event.trigger('ajaxStop');
				
				if (ajax.track) {
					var prevSearch = $('<div class="jsmart5_search_close"><span>' + element.val() + '</span><a onClick="Jsmart5.removeSearch(\'' + id + '\',$(this));"></a></div>');
					element.parent().before(prevSearch);
					element.attr('value', '');
	
					if (element.width() - prevSearch.outerWidth() > minWidth) {
						element.width(element.width() - prevSearch.outerWidth());
					} else {
						element.width(minWidth);
					}
				}
			};

			if (closestForm && closestForm.length > 0) {
				doFormPlaceHolders(closestForm);
				$(closestForm).ajaxSubmit(options);
			} else {
				$.ajax(options);
			}
			return false;
		}
	}

	function doRemoveSearch(id, close) {
		var input = $(getId(id));
		if (input.width() + close.parent().outerWidth() < input.attr('defaultWidth')) {
			input.width(input.width() + close.parent().outerWidth());
		} else {
			input.width(input.attr('defaultWidth'));
		}
		close.parent().remove();
	}

	function getId(id) {
		if (id) {
			id = '#' + id;
		}
		return id;
	}

	function getInlineStyle(element, property) {
		var value = null;
        var styles = element.attr('style');
        if (styles && styles.length > 0) {
        	styles.split(";").forEach(function (item) {
                var style = item.split(":");
                if (style.length > 1) {
                	if ($.trim(style[0]) === property) {
                		value = style[1];
                		return;
                	}
                }
            });
        }
        return value;
	}

	function replaceAll(string, token, newtoken) {
	    while (string.indexOf(token) != -1) {
	        string = string.replace(token, newtoken);
	    }
	    return string;
	}

	$.ui.dialog.prototype.options.listenEnter = "";
	
	function contains(array, element) {
		for (var i = 0; i < array.length; i++) {
			if (array[i] == element) {
				return true;
			}
		}
		return false;
	}
	
	function startsWith(string, str) {
		return string.match("^"+str) == str;
	}
	
	function endsWith(string, str) {
		return string.match(str+"$") == str;
	}
	
	function compare(arrayOne, arrayTwo) {
	    // if the other array is a falsy value, return
	    if (!arrayOne || !arrayTwo)
	        return false;
	
	    // compare lengths - can save a lot of time
	    if (arrayOne.length != arrayTwo.length)
	        return false;
	
	    for (var i = 0; i < arrayOne.length; i++) {
	        // Check if we have nested arrays
	        if (arrayOne[i] instanceof Array && arrayTwo[i] instanceof Array) {
	            // recurse into the nested arrays
	            if (!arrayOne[i].compare(arrayTwo[i]))
	                return false;
	        }
	        else if (arrayOne[i] != arrayTwo[i]) {
	            // Warning - two different object instances will never be equal: {x:20} != {x:20}
	            return false;
	        }
	    }
	    return true;
	}
	
	function isNumber(o) {
	    return typeof o == "number" || (typeof o == "object" && o.constructor === Number);
	}
	
	function isString(o) {
	    return typeof o == "string" || (typeof o == "object" && o.constructor === String);
	}
	
	function getDateSupported() {
		var lang = getLanguage();
		if (lang.indexOf('pt') != -1) {
			return 'pt-BR'
		} else {
			return '';
		}
	}
	
	function getLanguage() {
		if (navigator) {
		    if (navigator.language) {
		        return navigator.language;
	
		    } else if (navigator.browserLanguage) {
		        return navigator.browserLanguage;
	
		    } else if (navigator.systemLanguage) {
		        return navigator.systemLanguage;
	
		    } else if (navigator.userLanguage) {
		        return navigator.userLanguage;
		    }
		}
	}
	
	function showOnConsole(msg) {
		if (console && console.log) {
			console.log(msg);
		}
	}

})();
