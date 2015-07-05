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

	var tagInit = "j0";
	var tagJScroll = '010_';
	var tagJSelVal = '006_';
	var modalOpen = 'open()';
	var modalHide = 'close()';
	var refreshIcon = 'refresh-icon';
	var validateTextStyle = 'js5-validate-text';
	var validateGroupStyle = 'js5-validate-group';

	$(function () {
		initCheckboxes();
		initPopOvers();
		initTooltips();
	});
	
	function initPopOvers() {
		$('[data-toggle="popover"]').each(function() {
			var templateId = $(this).attr('template-id');

			if (templateId && templateId.length > 0) {
				$(this).popover({
					html: true, 
					content: function() {
						return $('#' + templateId).html();
					}
				});
			} else {
				$(this).popover();
			}
		});
	}
	
	function initTooltips() {
		$('[data-toggle="tooltip"]').each(function() {
			var templateId = $(this).attr('template-id');

			if (templateId && templateId.length > 0) {
				$(this).tooltip({
					html: true, 
					title: function() {
						return $('#' + templateId).html();
					}
				});
			} else {
				$(this).tooltip();
			}
		});
	}
	
	function initCheckboxes() {
		$('input:checkbox').each(function(index) {

			var id = $(this).attr('id');
			var value = $(this).val();
			var name = $(this).attr("name");

			if (!value || value == 'false') {
				$(this).after($('<input type="hidden" name="' + name + '" value="false" />'));
			}

			if (!id || id.length == 0) {
				return;
			}

			// Bind function to keep track of checkbox status
			$(document).on('click', '#' + id, function() {

				var value = $(this).val();
				var name = $(this).attr("name");
			
				if (!value || value == 'false') {
					$(this).attr('value', 'true');
					$("input:hidden[name='" + name + "']").each(function(index) {
						$(this).remove();
					});
				} else {
					$(this).attr('value', 'false');
					$(this).after($('<input type="hidden" name="' + name + '" value="false" />'));
				}
			});
		});
	}

	/******************************************************
	 * PUBLIC INTERFACE
	 ******************************************************/
	return {
		
		ajax: function(map, el) {
			doAjax(map, el);
		},
		
		bind: function(map) {
			doBind(map);
		},
			
		validate: function(id) {
			return doValidate(id);
		},
		
		execute: function(exec) {
			doExecute(exec);
		},
		
		modal: function(id) {
			openModal(id);
		},

		list: function(li, map) {
			doList(li, map);
		},
		
		listscroll: function(map) {
			doListScroll(map);
		},
		
		tab: function(map) {
			doTab(map);
		},
		
		tabpane: function(li, map) {
			doTabPane(li, map);
		},
		
		carousel: function(id) {
			doCarousel(id);
		},
		
		date: function(map) {
			doDate(map);
		},

		table: function(tr, map) {
			doTable(tr, map);
		},

		tablescroll: function(map) {
			doTableScroll(map);
		},

		tableheader: function(map) {
			doTableHeader(map);
		},
		
		progressgroup: function(map) {
			doProgressGroup(map);
		},
		
		progressbar: function(map) {
			doProgressBar(map);
		},
		
		autocplt: function(map, evt) {
			doAutoComplete(map, evt);
		},

		autocpltscroll: function(map) {
			doAutoCompleteScroll(map);
		},

		asyncevent: function(map) {
		    doAsyncEvent(map);
		},

		showLoad: function(id) {
		    appendLoadIcon({id: id, tag: 'ajax'});
		},

		hideLoad: function(id) {
		    removeLoadIcon({id: id, tag: 'ajax'});
		}
	};

	/******************************************************
	 * PRIVATE FUNCTIONS
	 ******************************************************/
	
	function doAjax(map, el) {
		if (map.timeout && map.timeout > 0) {
			var timeout = map.timeout;
			map.timeout = null;
			setTimeout(function() {doAjax(map, el);}, timeout);

		} else {
		    if (map.tag && (map.tag == 'select' || map.tag == 'link' || map.tag == 'button'
		                        || map.tag == 'dropaction' || map.tag == 'function')) {
                el = $(getId(map.id));
            }

            var rest = el.closest('div[role="restrequest"]');
            if (rest && rest.length > 0) {
                doRest(map, rest);
                return;
            }

            // Validate form before creating ajax request
            var closestForm = el.closest('form');
            if (closestForm && closestForm.length > 0) {
                if (!doValidate($(closestForm).attr('id'))) {
                    return;
                }
            }

			if (map.method) {
				var options = getAjaxOptions(map);
				var elParam = getElementParam(el, false);
				var dlgParam = getDelegateParam(el, map);

				if (map.method == 'post') {
					var postParam = getAjaxParams(map);

					for (var i = 0; i < dlgParam.length; i++) {
						postParam.push({name: dlgParam[i].name, value: dlgParam[i].value});
					}

					if (closestForm && closestForm.length > 0) {
						if (map.tag && (map.tag == 'checkbox' || map.tag == 'select' || map.tag == 'checkgroup' 
								|| map.tag == 'radiogroup')) {
							if (elParam.length > 0 && elParam[0].value == null) {
								postParam.push({name: elParam[0].name, value: elParam[0].value});
							}
						}
					} else {
						for (var i = 0; i < elParam.length; i++) {
							postParam.push({name: elParam[i].name, value: elParam[i].value});
						}
						postParam = $.param(postParam);
					}
					options.data = postParam;
				}

				if (map.method == 'post' && closestForm && closestForm.length > 0) {
					$(closestForm).ajaxSubmit(options);
				} else {
					$.ajax(options);
				}
			} else {
				if (map.before) {
					doExecute(map.before);
				}
			}
		}
	}
	
	function doBind(map) {
		if (map.timeout && map.timeout > 0) {
			var timeout = map.timeout;
			map.timeout = null;
			setTimeout(function() {doBind(map);}, timeout);

		} else {
			doExecute(map.execute);
		}
	}

	function doRest(map, rest) {
		if (rest && rest.attr('role') == 'restrequest') {

            if (!doValidate(rest.attr('id'))) {
                return;
            }

            var queryParams = '';
            var endpoint = rest.attr('endpoint');

            var mappedParams = getAjaxParams(map);
            for (var i = 0; i < mappedParams.length; i++) {
                if (queryParams.length != 0) {
                    queryParams += '&'
                }
                queryParams += mappedParams[i].name + '=' + mappedParams[i].value;
            }

            var options = getAjaxOptions(map);
            options.type = rest.attr('method');
            options.url = endpoint + (queryParams.length > 0 ? (endpoint.indexOf('?') >= 0 ? '&' : '?') + queryParams : '');
            options.contentType = 'application/' + rest.attr('content-type');

            // jsonp settings
            if (rest.attr('cors')) {
                options.crossDomain = rest.attr('cors') === 'true';
            }
            if (options.crossDomain || rest.attr('callback')) {
                options.dataType = 'jsonp';
            }
            if (rest.attr('callback')) {
                options.jsonp = false;
                options.jsonpCallback = rest.attr('callback');
            } else if (options.crossDomain) {
                options.jsonp = true;
            }

            // body settings
            if (rest.attr('method') != 'get' && rest.attr('method') != 'head') {
                if (rest.attr('content-type') == 'json') {
                    options.data = getRestJsonBody(rest);

                } else if (rest.attr('content-type') == 'xml') {
                    options.data = getRestXmlBody(rest);
                }
            }
            $.ajax(options);
        }
	}

	function doList(li, map) {
		if (li && li.length > 0) {
			var postParam = getAjaxParams(map);
			var options = getAjaxOptions(map);
			var closestForm = $(li).closest('form');
			
			var jsonParam = {};
			jsonParam.size = li.closest('ul').attr('scroll-size');
			jsonParam.index = li.attr('scroll-index');

			for (var i = 0; i < postParam.length; i++) {
				// Look for J_SEL_VAL parameter to send the index clicked
				if (postParam[i].name.indexOf(tagInit + tagJSelVal) >= 0) {
					postParam[i].value = li.attr('list-index');
				}
				
				// Look for J_SCROLL parameter to send scroll values
				if (postParam[i].name.indexOf(tagInit + tagJScroll) >= 0) {
					postParam[i].value = JSON.stringify(jsonParam);
				}
			}

			if (closestForm && closestForm.length > 0) {
				if (!doValidate($(closestForm).attr('id'))) {
					return;
				}
			} else {
				postParam = $.param(postParam);			
			}
			options.data = postParam;

			if (closestForm && closestForm.length > 0) {
				$(closestForm).ajaxSubmit(options);
			} else {
				$.ajax(options);
			}
		}
	}
	
	function doListScroll(map) {
		$(getId(map.id)).scroll(function(e) {
			var ul = $(this);
			if (ul.scrollTop() + ul.outerHeight() >= ul[0].scrollHeight) {

				// Timeout is used because scroll is called more than one time
				setTimeout(function() {

					var scrollActive = ul.attr('scroll-active');
					if (scrollActive && scrollActive.length > 0) {
						return;
					}
	
					// Set scroll as active to avoid multiple requests
					ul.attr('scroll-active', 'true');
	
					var postParam = getAjaxParams(map);
					var closestForm = $(ul).closest('form');
	
					var lastChild = null;
					if (ul.find('a').length > 0) {
						lastChild = ul.find('a:last-child');
					} else {
						lastChild = ul.find('li:last-child');
					}
	
					var jsonParam = {};
					jsonParam.size = ul.attr('scroll-size');
					jsonParam.index = parseInt(lastChild.attr('list-index')) + 1;
					jsonParam.lastId = lastChild.attr('scroll-last-id');
	
					for (var i = 0; i < postParam.length; i++) {
						// Look for J_SCROLL parameter to send scroll values
						if (postParam[i].name.indexOf(tagInit + tagJScroll) >= 0) {
							postParam[i].value = JSON.stringify(jsonParam);
							break;
						}
					}
	
					if (closestForm && closestForm.length > 0) {
						if (!doValidate($(closestForm).attr('id'))) {
							return;
						}
					} else {
						postParam = $.param(postParam);			
					}
	
					var refreshClone = null
					var hiddenRefresh = ul.find('span[' + refreshIcon + ']').closest('li');
	
					// Append loading icon on list if it was configured
					if (hiddenRefresh && hiddenRefresh.length > 0) {
						refreshClone = hiddenRefresh.clone();
						ul.append(refreshClone);
						refreshClone.slideDown('fast');
					}
					
					// Remove scroll-active and refreshing icon
					map.complete = function() {
						if (refreshClone) {
						    refreshClone.slideUp('fast', function() {
						        refreshClone.remove();
						        ul.removeAttr('scroll-active');
						    });
						} else {
						    ul.removeAttr('scroll-active');
						}
					};
	
					// Function to append to list
					map.successHandler = function(data) {
						var newUl = $(data).find(getId(ul.attr('id')));
	
						if (newUl && newUl.length > 0) {
	
							var lastChild = null
							if (newUl.find('a').length > 0) {
								lastChild = newUl.find('a:last-child');
							} else {
								lastChild = newUl.find('li:last-child');
							}
	
							if (lastChild && lastChild.length > 0) {
								var lastIndex = lastChild.attr('list-index')
	
								// Case the returned ul has last index different than current
								if (lastIndex && (jsonParam.index - 1) != lastIndex) {
									if (ul.find('a').length > 0) {
										ul.append(newUl.find('a'));
									} else {
										if (refreshClone) {
											ul.append(newUl.find('li').not(':first'));
										} else {
											ul.append(newUl.find('li'));
										}
									}
								}
							}
						}
					};
	
					var options = getAjaxOptions(map);
					options.data = postParam;
	
					if (closestForm && closestForm.length > 0) {
						$(closestForm).ajaxSubmit(options);
					} else {
						$.ajax(options);
					}
				}, 50);
			}
		});
	}
	
	function doTable(tr, map) {
		if (tr && tr.length > 0) {

			var refreshTr = tr.find('span[' + refreshIcon + ']');
			if (refreshTr && refreshTr.length > 0) {
				return;
			}

			var headTr = tr.find('th');
			if (headTr && headTr.length > 0) {
				return;
			}

			var postParam = getAjaxParams(map);
			var options = getAjaxOptions(map);
			var closestForm = $(tr).closest('form');
			
			// Table adapter parameters
			var jsonParam = {};
			var thead = tr.closest('table').find('thead tr');

			jsonParam.size = tr.closest('tbody').attr('scroll-size');
			jsonParam.index = tr.attr('scroll-index');
			jsonParam.lastId = tr.attr('scroll-last-id');
			
			var sortSpan = thead.find('span[sort-active]');
			if (sortSpan && sortSpan.length > 0) {
				jsonParam.sort = sortSpan.attr('sort-by');
				jsonParam.order = sortSpan.attr('sort-order');
			} else {
				jsonParam.sort = null;
				jsonParam.order = 0;
			}

			jsonParam.filters = {};
			thead.find('input').each(function() {
				jsonParam.filters[$(this).attr('filter-by')] = $(this).val();
			});

			for (var i = 0; i < postParam.length; i++) {
				// Look for J_SEL_VAL parameter to send the index clicked
				if (postParam[i].name.indexOf(tagInit + tagJSelVal) >= 0) {
					postParam[i].value = tr.attr('table-index');
				}
				
				// Look for J_SCROLL parameter to send scroll values
				if (postParam[i].name.indexOf(tagInit + tagJScroll) >= 0) {
					postParam[i].value = JSON.stringify(jsonParam);
				}
			}

			if (closestForm && closestForm.length > 0) {
				if (!doValidate($(closestForm).attr('id'))) {
					return;
				}
			} else {
				postParam = $.param(postParam);			
			}
			options.data = postParam;

			if (closestForm && closestForm.length > 0) {
				$(closestForm).ajaxSubmit(options);
			} else {
				$.ajax(options);
			}
		}
	}

	function doTableScroll(map) {
		var table = $(getId(map.id));

		var thead = table.find('thead tr');
		table.find('tbody tr:last').find('td').each(function(index) {
			thead.children().eq(index).css({'width': $(this).width()});
		});

		table.find('tbody').scroll(function(e) {
			var tbody = $(this);
			if (tbody.scrollTop() + tbody.outerHeight() >= tbody[0].scrollHeight) {

				// Timeout is used because scroll is called more than one time
				setTimeout(function() {

					// Table adapter parameters
					var jsonParam = {};
					var thead = tbody.closest('table').find('thead tr');
					var sortSpan = thead.find('span[sort-active]');
	
					if (sortSpan && sortSpan.length > 0) {
						jsonParam.sort = sortSpan.attr('sort-by');
						jsonParam.order = sortSpan.attr('sort-order');
					} else {
						jsonParam.sort = null;
						jsonParam.order = 0;
					}
	
					jsonParam.filters = {};
					thead.find('input').each(function() {
						jsonParam.filters[$(this).attr('filter-by')] = $(this).val();
					});
	
					doTableAjax(tbody, map, false, jsonParam);
				}, 50);
			}
		});
	}

	function doTableHeader(map) {
		// For sorting
		$(document).on('click', getId(map.id) + ' span', function(e) {

			var sortActive = $(this).attr('sort-active');
			if (sortActive && sortActive.length > 0) {
				return;
			}

			$(this).closest('tr').find('span').removeAttr('sort-active');
			$(this).attr('sort-active', 'true');

			// Table adapter parameters
			var jsonParam = {};
			jsonParam.sort = $(this).attr('sort-by');
			jsonParam.order = $(this).attr('sort-order');
			jsonParam.filters = {};

			// Get all filters
			$(this).closest('tr').find('input').each(function() {
				jsonParam.filters[$(this).attr('filter-by')] = $(this).val();
			});

			var tbody = $(this).closest('table').find('tbody');
			
			doTableAjax(tbody, map, true, jsonParam);
		});

		// For filters
		$(document).on('keyup', getId(map.id) + ' input', function(e) {
			var input = $(this);
			var thead = input.closest('tr');
			var tbody = input.closest('table').find('tbody');

			thead.find('input').each(function() {
				var timeoutId = $(this).attr('filter-timeout');

				if (timeoutId && timeoutId.length > 0) {
					clearTimeout(timeoutId);
					$(this).removeAttr('filter-timeout');
				}
			});

			// Timeout to avoid sending ajax per key typed
			var timeoutId = setTimeout(function() {
				
				// Table adapter parameters
				var jsonParam = {};
				var sortSpan = thead.find('span[sort-active]');

				if (sortSpan && sortSpan.length > 0) {
					jsonParam.sort = sortSpan.attr('sort-by');
					jsonParam.order = sortSpan.attr('sort-order');
				} else {
					jsonParam.sort = null;
					jsonParam.order = 0;
				}

				jsonParam.filters = {};
				thead.find('input').each(function() {
					jsonParam.filters[$(this).attr('filter-by')] = $(this).val();
				});

				doTableAjax(tbody, map, true, jsonParam);
			}, 2000);
			
			input.attr('filter-timeout', timeoutId);
		});
	}
	
	function doTableAjax(tbody, map, reset, jsonParam) {

		var scrollActive = tbody.attr('scroll-active');
		if (scrollActive && scrollActive.length > 0) {
			return;
		}

		// Set scroll as active to avoid multiple requests
		tbody.attr('scroll-active', 'true');
		
		var postParam = getAjaxParams(map);
		var closestForm = $(tbody).closest('form');

		// Set the jsonParam values as size and index
		jsonParam.size = tbody.attr('scroll-size');

		if (reset) {
			jsonParam.index = 0;
		} else {
			var lastChild = tbody.find('tr:last-child');		
			jsonParam.index = parseInt(lastChild.attr('table-index')) + 1;
		}

		for (var i = 0; i < postParam.length; i++) {
			// Look for J_SCROLL parameter to send scroll values
			if (postParam[i].name.indexOf(tagInit + tagJScroll) >= 0) {
				postParam[i].value = JSON.stringify(jsonParam);
				break;
			}
		}

		if (closestForm && closestForm.length > 0) {
			if (!doValidate($(closestForm).attr('id'))) {
				return;
			}
		} else {
			postParam = $.param(postParam);			
		}

		var refreshClone = null
		var hiddenRefresh = tbody.find('span[' + refreshIcon + ']').closest('tr');

		// Append loading icon on list if it was configured
		if (hiddenRefresh && hiddenRefresh.length > 0) {
			refreshClone = hiddenRefresh.clone();
			refreshClone.find('td').css({'display': 'block'});
			tbody.append(refreshClone);
			refreshClone.slideDown('fast');
		}

		// Remove scroll-active and refreshing icon
		map.complete = function() {
			if (refreshClone) {
			    refreshClone.slideUp('fast', function() {
			        refreshClone.remove();
                    tbody.removeAttr('scroll-active');
			    });
			} else {
			    tbody.removeAttr('scroll-active');
			}
		};

		// Function to append to table body
		map.successHandler = function(data) {
			var newTable = $(data).find(getId(map.id));
			if (newTable && newTable.length > 0) {

				// Case reset replace the tbody content
				if (reset) {
					tbody.empty().append(newTable.find('tbody tr'));
					return;
				}

				// Case not reset it will append the result on tbody
				var lastChild = newTable.find('tbody tr:last-child');

				if (lastChild && lastChild.length > 0) {
					var lastIndex = lastChild.attr('table-index')

					// Case the returned table has last index different than current
					if (lastIndex && (jsonParam.index - 1) != lastIndex) {
						if (refreshClone) {
							tbody.append(newTable.find('tbody tr').not(':first'));
						} else {
							tbody.append(newTable.find('tbody tr'));
						}
					}
				}
			}
		};

		var options = getAjaxOptions(map);
		options.data = postParam;

		if (closestForm && closestForm.length > 0) {
			$(closestForm).ajaxSubmit(options);
		} else {
			$.ajax(options);
		}
	}

	function doTab(map) {
		var tabElement = $(getId(map.id));

		// Get active tab via hidden input name sent on map
		var tabValue = $('input[name="' + map.params[0].name + '"]').val();
		if (tabValue && tabValue.length > 0) {
			var tabPane = tabElement.find('>ul li[tab-value="' + tabValue + '"]');
			tabPane.find('>a').tab('show');
			tabPane.closest('li.dropdown').addClass('active');
		}
	}
	
	function doTabPane(li, map) {
		if (li && li.length > 0) {
			
			// Case is dropdown menu do not send the tab value
			if (li.find('ul.dropdown-menu').length > 0) {
				return;
			}
			
			var tabElement = $(getId(map.id));
			var inputName = $.trim(map.params[0].name.replace(/\"/g, ''));
	
			// Get active tab via hidden input name sent on map
			var tabInput = $('input[name="' + inputName + '"]');
			tabInput.val(li.attr('tab-value'));

			var postParam = getAjaxParams(map);
			var options = getAjaxOptions(map);
			var closestForm = li.closest('form');

			// Set the hidden input value
			postParam[0].value = tabInput.val();

			if (closestForm && closestForm.length > 0) {
				if (!doValidate($(closestForm).attr('id'))) {
					return;
				}
			} else {
				postParam = $.param(postParam);			
			}

			options.data = postParam;

			if (closestForm && closestForm.length > 0) {
				$(closestForm).ajaxSubmit(options);
			} else {
				$.ajax(options);
			}
		}
	}

	function doCarousel(id) {
		var el = $(getId(id));
		var active = el.find('div.active');

		el.find('>ol li').removeClass('js5-carousel-indicator-active');
		
		if (active.find('>img').length > 0) {
			el.find('>ol li').removeClass('js5-carousel-indicator');
			el.find('>a').removeClass('js5-carousel-control');
		} else {
			el.find('>ol li').addClass('js5-carousel-indicator');
			el.find('>ol li.active').addClass('js5-carousel-indicator-active');
			el.find('>a').addClass('js5-carousel-control');
		}
	}
	
	function doDate(map) {
		var dateOptions = {};
		dateOptions.showTodayButton = true;
		dateOptions.calendarWeeks = map.showWeeks;

		if (map.locale && map.locale.length > 0) {
			dateOptions.locale = map.locale;
		}
		if (map.format && map.format.length > 0) {
			dateOptions.format = map.format;
		}
		if (map.viewMode && map.viewMode.length > 0) {
			dateOptions.viewMode = map.viewMode;
		}

		var inputDate = $(getId(map.id));
		var hiddenDate = $(getId(map.id) + '-date');

		inputDate.datetimepicker(dateOptions);

		// Initial hidden field update when page is loaded
		if (hiddenDate.val() && hiddenDate.val().length > 0) {
			hiddenDate.val(new Date(hiddenDate.val()).getTime());
		}

		// Update hidden field on change with date in milliseconds
		inputDate.on('dp.change', function(event) {
		    if (event.date) {
			    hiddenDate.val(event.date.valueOf());
			} else {
			    hiddenDate.val(null);
			}
		});

		if (map.linkDate && map.linkDate.length > 0) {
			inputDate.on('dp.change', function(event) {
				$(getId(map.linkDate)).data('DateTimePicker').maxDate(event.date);
			});

			$(getId(map.linkDate)).on('dp.change', function (event) {
				inputDate.data('DateTimePicker').minDate(event.date);
			});
		}
	}

	function doProgressGroup(map) {
		var div = $(getId(map.id));
		var bars = div.find('div[role="progressbar"]');

		if (bars && bars.length > 0) {
			var index = 0;

			var intervalId = setInterval(function() {
				var bar = $(bars[index]);
				
				if (handleProgressBar(bar, map, map.relation[index])) {

					if (index == bars.length -1) {
						clearInterval(div.attr('interval-id'));
					} else {
						index++;
					}
				}
			}, map.interval);

			div.attr('interval-id', intervalId);
		}
	}
	
	function doProgressBar(map) {
		var bar = $(getId(map.id));
		var intervalId = setInterval(function() {

			if (handleProgressBar(bar, map)) {
				clearInterval(bar.attr('interval-id'));
			}
		}, map.interval);

		bar.attr('interval-id', intervalId);
	}
	
	function handleProgressBar(bar, map, relation) {
		var input = null;
		var name = bar.attr('name');

		if (name && name.length > 0) {
			input = $('input[name="' + name + '"]');
		}

		// If request is true, need to get data from bean
		if (map.request == true) {

			var options = getAjaxOptions(map);
			options.async = false;

			// Do not use map.successHandler here, otherwise it will refresh the page continuesly
			options.success = function(data) {
				var newInput = $(data).find('input[name="' + bar.attr('name') + '"]');

				if (newInput && newInput.length > 0) {

					// Input must exist in this case
					input.val(newInput.val());
					bar.attr('aria-valuenow', newInput.val());
				}
			}

			$.ajax(options);
		}

		var value = parseInt(bar.attr('aria-valuenow'));
		var minValue = parseInt(bar.attr('aria-valuemin'));
		var maxValue = parseInt(bar.attr('aria-valuemax'));

		var callback = window[map.onInterval];

		if (typeof callback === 'function') {
			value = callback(bar, value, minValue, maxValue);

			if (value && value === parseInt(value)) {

				// Keep the constraints valid
				if (value < minValue) {
					value = minValue;
				}
				if (value > maxValue) {
					value = maxValue;
				}
				bar.attr('aria-valuenow', value);
				
				if (input && input.length > 0) {
					input.val(value);
				}
			}
		}

		// Get the value back case it is changed by callback
		value = parseInt(bar.attr('aria-valuenow'));

		var percent = ((100 * (value - minValue) / (maxValue - minValue)) | 0);

		// Calculate the percentage related to its relation
		if (relation) {
			percent = ((percent * relation / 100) | 0);
		}

		bar.css({'width': percent + '%'});
		
		// Check if progress bar is using label before updating it
		if (bar.text().indexOf('%') >= 0) {
			bar.text(percent + '%');
		}

		return value >= maxValue;
	}

	function doAutoComplete(map, evt) {
		var input = $(getId(map.id));
		var inputRefresh = $('span[auto-refresh-id="' + map.id + '"]');
		
		var ul = $('ul[auto-list-id="' + map.id + '"]');
		var ulRefresh = ul.find('span[' + refreshIcon + ']').closest('li');
		
		var timer = input.attr('timeout-id');
		if (timer && timer.length > 0) {
		    clearTimeout(timer);
		}

		var value = input.val();
		
		// If space or length less than minLength just return
		if (evt.keyCode == 32 || $.trim(value).length < input.attr('min-length')) {
		    return;
		}

		var leftUl = input.position().left + parseInt(input.css('marginLeft').replace('px', ''));
		var topUl = input.position().top + input.outerHeight(true) + 5;
		var widthUl = input.outerWidth();

		var leftRefresh = input.outerWidth() - inputRefresh.outerWidth() - 10;
		var topRefresh = input.position().top + ((input.height()) / 2);

		var inputGroup = input.closest('div.input-group');
		if (inputGroup && inputGroup.length > 0) {
		    leftUl = inputGroup.position().left + parseInt(inputGroup.css('marginLeft').replace('px', ''));
		    topUl = inputGroup.position().top + inputGroup.outerHeight(true) + 5;
		    widthUl = inputGroup.outerWidth();
		
		    leftRefresh += inputGroup.find('div.input-group-addon:first').outerWidth();
		}

		timer = setTimeout(function() {
		
		    inputRefresh.css({'left': leftRefresh, 'top': topRefresh});
		    inputRefresh.show();
		
		    var postParam = getAjaxParams(map);
		    var closestForm = input.closest('form');
		
		    // Push the input content so we send the value to be auto completed
		    postParam.push({name: input.attr('name'), value: input.val()});
		
		    if (closestForm && closestForm.length > 0) {
		        if (!doValidate($(closestForm).attr('id'))) {
		            return;
		        }
		    } else {
		        postParam = $.param(postParam);
		    }

			map.successHandler = function(data) {
			
			    ul.css({'position': 'absolute',
			            'left': leftUl,
			            'top': topUl,
			            'width': widthUl,
			            'z-index': 10
			        });

				// Empty list but include the load if it was present
				ul.empty();
				if (ulRefresh && ulRefresh.length > 0) {
				    ul.append(ulRefresh);
				}
				ul.append($(data).find('ul[auto-list-id="' + map.id + '"] a'));
				
				// Only the first click is bound
				$(window).one('click', function() {
				    ul.hide();
				});
				
				// Only the first click is bound
				ul.one('click', 'a', function() {
				    input.val($(this).attr('to-string'));
				});
				ul.show();
			}
			
			map.completeHandler = function(xhr, status) {
			    inputRefresh.hide();
			}

		    var options = getAjaxOptions(map);
		    options.data = postParam;
		
		    if (closestForm && closestForm.length > 0) {
		        $(closestForm).ajaxSubmit(options);
		    } else {
		        $.ajax(options);
		    }
		}, 1000);

	    input.attr('timeout-id', timer);
	}

	function doAutoCompleteScroll(map) {
		$('ul[auto-list-id="' + map.id + '"]').scroll(function(e) {
			var ul = $(this);
			if (ul.scrollTop() + ul.outerHeight() >= ul[0].scrollHeight) {

                // Timeout is used because scroll is called more than one time
                setTimeout(function() {

					var scrollActive = ul.attr('scroll-active');
					if (scrollActive && scrollActive.length > 0) {
                        return;
                    }

					// Set scroll as active to avoid multiple requests
					ul.attr('scroll-active', 'true');

					var postParam = getAjaxParams(map);
					var closestForm = $(ul).closest('form');

					// Push the input content so we send the value to be auto completed
					var input = $(getId(map.id));
					postParam.push({name: input.attr('name'), value: input.val()});

					var lastChild = ul.find('a:last-child');

					var jsonParam = {};
					jsonParam.size = ul.attr('scroll-size');
					jsonParam.index = parseInt(lastChild.attr('list-index')) + 1;
					jsonParam.lastId = lastChild.attr('scroll-last-id');

					for (var i = 0; i < postParam.length; i++) {
						// Look for J_SCROLL parameter to send scroll values
						if (postParam[i].name.indexOf(tagInit + tagJScroll) >= 0) {
							postParam[i].value = JSON.stringify(jsonParam);
							break;
						}
					}

					if (closestForm && closestForm.length > 0) {
						if (!doValidate($(closestForm).attr('id'))) {
							return;
						}
					} else {
						postParam = $.param(postParam);
					}

					var refreshClone = null
					var hiddenRefresh = ul.find('span[' + refreshIcon + ']').closest('li');

					// Append loading icon on list if it was configured
					if (hiddenRefresh && hiddenRefresh.length > 0) {
						refreshClone = hiddenRefresh.clone();
						ul.append(refreshClone);
						refreshClone.slideDown('fast');
					}

					// Remove scroll-active and refreshing icon
					map.complete = function() {
						if (refreshClone) {
						    refreshClone.slideUp('fast', function() {
						        refreshClone.remove();
						        ul.removeAttr('scroll-active');
						    });
						} else {
						    ul.removeAttr('scroll-active');
						}
					};

					// Function to append to list
					map.successHandler = function(data) {
						var newUl = $(data).find('ul[auto-list-id="' + map.id + '"]');

                        if (newUl && newUl.length > 0) {
							var lastChild = newUl.find('a:last-child');

							if (lastChild && lastChild.length > 0) {
								var lastIndex = lastChild.attr('list-index')

								// Case the returned ul has last index different than current
                                if (lastIndex && (jsonParam.index - 1) != lastIndex) {
                                    if (ul.find('a').length > 0) {
										ul.append(newUl.find('a'));
                                    }
                                }
                            }
                        }
					};

					var options = getAjaxOptions(map);
					options.data = postParam;

					if (closestForm && closestForm.length > 0) {
						$(closestForm).ajaxSubmit(options);
					} else {
						$.ajax(options);
					}
				}, 50);
			}
		});
	}

	function doAsyncEvent(map) {
	    if (map) {
	        if (typeof(EventSource) === "undefined") {
                showOnConsole('Your browser do not support EventSource for async events');
                return;
            }

	        var source = null;
	        if (map.credentials) {
	            source = new EventSource(map.path, map.credentials);
	        } else {
	            source = new EventSource(map.path);
	        }

            for (var i = 0; i < map.events.length; i++) {
                if (map.events[i].execute && map.events[i].execute.length > 0) {
                    var eventListener = window[map.events[i].execute];

                    if (typeof eventListener === 'function') {
                        source.addEventListener(map.events[i].event, eventListener,
                                map.events[i].capture != null ? map.events[i].capture : false);
                    } else {
                        showOnConsole('Found error on async [' + map.id + ']. The [' + map.events[i].execute + '] execute attribute is '
                                        + 'not a function. Please provide a function with event parameter');
                    }
                }
            }

            if (map.start && map.start.length > 0) {
                var onStart = window[map.start];
                if (typeof onStart === 'function') {
                    onStart(source, map.id);
                } else {
                    showOnConsole('Found error on async [' + map.id + ']. The [' + map.start + ' is not a function');
                }
            }
	    }
	}

	/******************************************************
	 * REST FUNCTIONS
	 ******************************************************/

	function getRestJsonBody(rest) {
        var json = '';
        var root = rest.attr('body-root');

        if (root && root.length > 0) {
            json += '{\"' + root.replace(/"/g, '\\"') + '\":';
        }
        json += getRestJsonItem(rest);

        if (root && root.length > 0) {
            json += '}';
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
                            if (isString(values[i])) {
                                json += '\"' + values[i].replace(/"/g, '\\"') + '\",';
                            } else {
                                json += '\"' + values[i] + '\",';
                            }
						}
						json = json.substring(0, json.length - 1);
					}
					json += '],';
				} else {
					if (elementParam[0].value || elementParam[0].value == false) {
					    if (isString(elementParam[0].value)) {
					        json += '\"' + rest + '\":\"' + elementParam[0].value.replace(/"/g, '\\"') + '\",';
					    } else {
					        json += '\"' + rest + '\":\"' + elementParam[0].value + '\",';
					    }
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

	function getRestXmlBody(rest) {
		var xml = '<?xml version="1.0" encoding="UTF-8" ?>';
		var root = rest.attr('body-root');

		if (root && root.length > 0) {
			xml += '<' + root.replace(/>/g, '&gt;').replace(/</g, '&lt;') + '>';
		} else {
			xml += '<root>';
		}

        xml += getRestXmlItem(rest);

		if (root && root.length > 0) {
			xml += '</' + root.replace(/>/g, '&gt;').replace(/</g, '&lt;') + '>';
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
						    if (isString(values[i])) {
						        var value = values[i].replace(/>/g, '&gt;').replace(/</g, '&lt;');
						        xml += '<' + rest + '>' + value + '</' + rest + '>';
						    } else {
						        xml += '<' + rest + '>' + values[i] + '</' + rest + '>';
						    }
						}
					}
				} else {
					if (elementParam[0].value || elementParam[0].value == false) {
					    if (isString(elementParam[0].value)) {
					        var value = elementParam[0].value.replace(/>/g, '&gt;').replace(/</g, '&lt;');
					        xml += '<' + rest + '>' + value + '</' + rest + '>';
					    } else {
                            xml += '<' + rest + '>' + elementParam[0].value + '</' + rest + '>';
					    }

					} else {
						xml += '<' + rest + ' />';
					}
				}
			}
		});
		return xml;
	}
	
	/******************************************************
	 * AJAX FUNCTIONS
	 ******************************************************/
	
	function getAjaxOptions(map) {
		return {
			type: map.method, 
			url: $(location).attr('href') + ($(location).attr('href').indexOf('?') >= 0 ? '&' : '?') + new Date().getTime(),
			beforeSend: function (xhr, settings) {
				appendLoadIcon(map);
				doHeaders(map, xhr, settings);
				doExecute(map.before, xhr, settings);
			},
			success: function (data, status, xhr) {

				var reset = xhr.getResponseHeader("Reset-Ajax"); 
				if (reset && reset.length > 0) {
					$(location).attr('href', $(location).attr('href'));

				} else {
					if (map.url && map.url.length > 0) {
						$(location).attr('href', map.url);
					} else {
						doExecute(map.successHandler, data, xhr, status);
						doUpdate(map.update, data);
						doExecute(map.success, data, xhr, status);
						doAlertCheck(data);
						
						var redirect = xhr.getResponseHeader("Redirect-Ajax"); 
						if (redirect && redirect.length > 0) {
							$(location).attr('href', redirect);
						}
					}
				}
			},
			error: function (xhr, status, error) {
			    doExecute(map.errorHandler, xhr, status, error);
				doExecute(map.error, xhr, status, error);
				showOnConsole(status);
				showOnConsole(error);
			},
			complete: function (xhr, status) {
				removeLoadIcon(map);
				doExecute(map.completeHandler, xhr, status);
				doExecute(map.complete, xhr, status);
			},
			async: true
		};
	}
	
	function getAjaxParams(map) {
		var params = [];
		if (map.action) {
			var action = $.trim(map.action);
			params.push({name: action, value: 0});
		}
		
		if (map.args) {
			for (var i = 0; i < map.args.length; i++) {
				if (map.args[i].value !== undefined) {
					var name = $.trim(map.args[i].name);
					params.push({name: name, value: map.args[i].value});
				}
			}
		}

		if (map.params) {
			for (var i = 0; i < map.params.length; i++) {
				if (map.params[i].value !== undefined) {
					var name = $.trim(map.params[i].name);
					params.push({name: name, value: map.params[i].value});
				}
			}
		}
		return params;
	}

	function getDelegateParam(el, map) {
		var dlgParam = [];
		
		if (map.params) {
			for (var i = 0; i < map.params.length; i++) {
				if (map.params[i].value === undefined) {
					var name = $.trim(map.params[i].name);
					var value = el.attr(name);
					dlgParam.push({name: name, value: value});
				}
			}
		}
		
		if (map.args) {
			for (var i = 0; i < map.args.length; i++) {
				if (map.args[i].value === undefined) {
					var name = $.trim(map.args[i].name);
					var value = el.attr(name);
					if (value && value.length > 0) {

						var values = $.parseJSON(value.replace(/\'/g, '"'));
						for (var j = 0; j < values.length; j++) {
							dlgParam.push({name: name, value: values[j]});
						}
					} else {
						dlgParam.push({name: name, value: null});
					}
				}
			}
		}
		return dlgParam;
	}

	function getElementParam(el, rest) {
		var elParams = [];
		var name = $(el).attr('name');
		var checkgroups = new Array();
	
		if ($(el).is('select') && $(el).attr('multiple')) {
			var values = $(el).val();
			if (values && values.length > 0) {
				if (rest) {
					var value = "";
					for (var i = 0; i < values.length; i++) {
						value += values[i] + ",";
					}
					if (value.length > 0) {
						elParams.push({name: name, value: value.substring(0, value.length - 1), array: true});
					}
				} else {
					for (var i = 0; i < values.length; i++) {
						elParams.push({name: name, value: values[i], array: true});
					}
				}
			}
	
			if (elParams.length == 0) {
				elParams.push({name: name, value: null, array: true});
			}
		} else if ($(el).is('input') && $(el).attr('checkgroup')) {
			if (!contains(checkgroups, name)) {
				checkgroups[checkgroups.length] = name;
	
				var values = [];
				$(el).parents('div[checkgroup]').find("input:checked[name='" + name + "']").each(function(index) {
					values.push($(this).val());
				});
	
				if (rest) {
					var value = "";
					for (var i = 0; i < values.length; i++) {
						value += values[i] + ",";
					}
					if (value.length > 0) {
						elParams.push({name: name, value: value.substring(0, value.length - 1), array: true});
					}
				} else {
					for (var i = 0; i < values.length; i++) {
						elParams.push({name: name, value: values[i], array: true});
					}
				}
	
				if (elParams.length == 0) {
					elParams.push({name: name, value: null, array: true});
				}
			}
		} else if ($(el).is('input') && $(el).attr('radiogroup')) {
			if (!contains(checkgroups, name)) {
				checkgroups[checkgroups.length] = name;
	
				var val = $(el).parents('div[radiogroup]').find("input:checked[name='" + name + "']").val();
				elParams.push({name: name, value: val, array: false});
			}
		} else if ($(el).is('input:checkbox')) {
			var value = $(el).val();
	
			if (!value || value == 'false') {
				elParams.push({name: name, value: false, array: false});
			} else {
				elParams.push({name: name, value: true, array: false});
			}
		} else if ($(el).is('input') && $(el).attr('date')) {
			var date = $('input[id^="' + $(el).attr('id') + '"]:hidden');

			if (date && date.val()) {
				elParams.push({name: date.attr('name'), value: date.val(), array: false});
			}
		} else {
			var value = $(el).val();
			if (value) {
				elParams.push({name: name, value: $(el).val(), array: false});
			}
		}

		return elParams;
	}

	function doHeaders(map, xhr, settings) {
		var values = map.id;
		if (map.update && map.update.length > 0) {
			values += ',' + map.update;
		}
		xhr.setRequestHeader('Update-Ajax', values);
	}

	function doUpdate(update, a) {
		if (update && update.length > 0) {
			var updates = update.split(',');

			for (var i = 0; i < updates.length; i++) {
				var updateId = getId(updates[i]);
				$(updateId).replaceWith($(a).find(updateId));
			}
		}
	}
	
	function doExecute(func, a, b, c) {
		var showModals = [];
		var hideModals = [];
		var callbacks = [];
		var execute = '';

		if (typeof func === 'function') {
			callbacks.push(func);

		} else if (func && func.length > 0) {
			var funcs = func.split(';');
	
			for (var i = 0; i < funcs.length; i++) {
				if (funcs[i].indexOf(modalOpen) >= 0) {
					showModals.push(funcs[i].substring(0, funcs[i].indexOf('.')));
	
				} else if (funcs[i].indexOf(modalHide) >= 0) {
					hideModals.push(funcs[i].substring(0, funcs[i].indexOf('.')));

				} else {
					var callback = window[funcs[i]];

					if (typeof callback === 'function') {
						callbacks.push(callback);
					} else {
						execute += funcs[i] + ";";
					}
				}
			}
		}

		for (var i = 0; i < callbacks.length; i++) {
			try {
				callbacks[i](a, b, c);
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
		
		for (var i = 0; i < hideModals.length; i++) {
			hideModal(hideModals[i]);
		}
		for (var i = 0; i < showModals.length; i++) {
			openModal(showModals[i]);
		}
		return showModals;
	}

	function doAlertCheck(data) {
		$('div[role="alert-wrap"]').hide();

		$(data).find('div[alert-show]').each(function() {
			doUpdate($(this).attr('id'), data);
			$(this).show();
		});
	}

	function appendLoadIcon(map) {
		if (map.tag && (map.tag == 'button' || map.tag == 'link' || map.tag == 'ajax')) {
			var el = $(getId(map.id));

			var hiddenRefresh = el.find('span[' + refreshIcon + ']');
			if (hiddenRefresh && hiddenRefresh.length > 0) {

				var leftIcon = el.find('span.glyphicon[side="left"]:first');
				if (leftIcon && leftIcon.length > 0) {
					leftIcon.hide();
				}

				var refreshClone = hiddenRefresh.clone();
				refreshClone.css({'margin-right': '4px'});
				el.prepend(refreshClone.show());
			}
		}
	}

	function removeLoadIcon(map) {
		if (map.tag && (map.tag == 'button' || map.tag == 'link' || map.tag == 'ajax')) {
			var el = $(getId(map.id));
			
			var refreshClone = el.find('span[' + refreshIcon + ']:first');
			if (refreshClone && refreshClone.length > 0) {
				
				refreshClone.remove();

				var leftIcon = el.find('span.glyphicon[side="left"]:first');
				if (leftIcon && leftIcon.length > 0) {
					leftIcon.show();
				}
			}
		}
	}

	/******************************************************
	 * VALIDATE FUNCTIONS
	 ******************************************************/

	function doValidate(id) {
		var validated = true;
		if (id && id.length > 0) {
			var validateElement = $(getId(id));
			var checkgroups = new Array();
	
			validateElement.find('em[vldt-ref]').remove();
	
			validateElement.find('*[vldt-req]').each(function(index) {

                var text = $(this).attr('vldt-text');
                var look = 'has-' + $(this).attr('vldt-look');
                var regex = $(this).attr('vldt-regex');
                var value = getElementParam($(this), true);

                var textLook = 'text-' + $(this).attr('vldt-look');
                if (textLook.indexOf('error') >= 0) {
                    textLook = 'text-danger';
                }
                textLook += ' ' + validateTextStyle;

                if (regex && regex.length > 0) {
                    regex = new RegExp(regex, "i");
                }

                if ($(this).is('input') && $(this).attr('checkgroup')) {
                    var name = $(this).attr("name");

                    if (name && !contains(checkgroups, name)) {
                        checkgroups[checkgroups.length] = name;

                        // If checkgroup has label or it is inside rest or form we need to use the form-group class
                        if ($(this).closest('div.form-group').length > 0) {
                            $(this).closest('div.form-group').removeClass(look);
                        } else {
                            $(this).closest('div[checkgroup]').removeClass(look);
                        }

                        if (value.length == 0 || !value[0].value || value[0].value.length == 0) {

                            if ($(this).closest('div[checkgroup]').attr('inline')) {
                                textLook = textLook.replace(validateTextStyle, validateGroupStyle);
                            }
                            addValidate($(this), text, 'checkgroup', look, textLook);
                            validated = false;
                        }
                    }
                } else if ($(this).is('input') && $(this).attr('radiogroup')) {
                    var name = $(this).attr("name");

                    if (name && !contains(checkgroups, name)) {
                        checkgroups[checkgroups.length] = name;

                        // If radiogroup has label or it is inside rest or form we need to use the form-group class
                        if ($(this).closest('div.form-group').length > 0) {
                            $(this).closest('div.form-group').removeClass(look);
                        } else {
                            $(this).closest('div[radiogroup]').removeClass(look);
                        }

                        if (value.length == 0 || !value[0].value || value[0].value.length == 0) {

                            if ($(this).closest('div[radiogroup]').attr('inline')) {
                                textLook = textLook.replace(validateTextStyle, validateGroupStyle);
                            }
                            addValidate($(this), text, 'radiogroup', look, textLook);
                            validated = false;
                        }
                    }
                } else if ($(this).is('input:checkbox')) {

                    $(this).closest('div.checkbox').removeClass(look);

                    if (value.length == 0 || !value[0].value || value[0].value == 'false') {
                        addValidate($(this), text, 'checkbox', look, textLook);
                        validated = false;
                    }
                } else {
                    var type = $(this).closest('div.form-group').length > 0 ? 'form-group' :
                               $(this).closest('div.input-group').length > 0 ? 'input-group' : 'default';

                    if (type == 'form-group') {
                        $(this).closest('div.form-group').removeClass(look);

                    } else if (type == 'input-group') {
                         $(this).closest('div.input-group').removeClass(look);

                    } else {
                        $(this).removeClass(look);
                    }

                    if ($(this).is('input:file')) {
                        if ($(this)[0].files.length > 0) {

                            var file = $(this)[0].files[0];
                            var fileSize = file.size || file.fileSize;
                            fileSize = parseInt(fileSize);

                            var minLength = $(this).attr('vldt-min-l');
                            if (minLength && fileSize < minLength) {
                                addValidate($(this), text, type, look, textLook);
                                validated = false;
                            }

                            var maxLength = $(this).attr('vldt-max-l');
                            if (maxLength && fileSize > maxLength) {
                                addValidate($(this), text, type, look, textLook);
                                validated = false;
                            }

                            if (regex && !regex.test(value[0].value)) {
                                addValidate($(this), text, type, look, textLook);
                                validated = false;
                            }

                        } else {
                            addValidate($(this), text, type, look, textLook);
                            validated = false;
                        }

                    } else if (value.length > 0 && value[0].value && value[0].value.length > 0) {

                        if (isString(value[0].value) && $.trim(value[0].value).length == 0) {
                            addValidate($(this), text, type, look, textLook);
                            validated = false;

                        } else {
                            var minLength = $(this).attr('vldt-min-l');
                            if (minLength && value[0].value.length < minLength) {
                                addValidate($(this), text, type, look, textLook);
                                validated = false;
                            }

                            var maxLength = $(this).attr('vldt-max-l');
                            if (maxLength && value[0].value.length > maxLength) {
                                addValidate($(this), text, type, look, textLook);
                                validated = false;
                            }
                        }

                        if (regex && !regex.test(value[0].value)) {
                            addValidate($(this), text, type, look, textLook);
                            validated = false;
                        }

                    } else {
                        addValidate($(this), text, type, look, textLook);
                        validated = false;
                    }
                }
			});
		}
		return validated;
	}
	
	function addValidate(element, text, type, look, textLook) {
		if (type == 'radiogroup' || type == 'checkgroup') {
		    // If radiogroup or checkgroup has label or it is inside rest or form we need to use the form-group class
		    if (element.closest('div.form-group').length > 0) {
		        element.closest('div.form-group').addClass(look);
		    } else {
			    element.closest('div[' + type + ']').addClass(look);
			}

			if (text && text.length > 0) {
				element.closest('div[' + type + ']').after($('<em vldt-ref=""></em>').addClass(textLook).text(text));
			}

		} else if (type == 'checkbox') {
			element.closest('div.checkbox').addClass(look);
			if (text && text.length > 0) {
				element.closest('div.checkbox').after($('<em vldt-ref=""></em>').addClass(textLook).text(text));
			}

		} else if (type == 'form-group' || type == 'input-group') {
			element.closest('div.' + type).addClass(look);
			if (text && text.length > 0) {
				element.closest('div.' + type).after($('<em vldt-ref=""></em>').addClass(textLook).text(text));
			}

		} else {
			element.addClass(look);
			if (text && text.length > 0) {
				element.after($('<em vldt-ref=""></em>').addClass(textLook).text(text));
			}
		}
	}

	/******************************************************
	 * MODAL FUNCTIONS
	 ******************************************************/
	
	function openModal(id) {
		$(getId(id)).modal('show');
	}
		
	function hideModal(id) {
		$(getId(id)).modal('hide');
	}

	/******************************************************
	 * GENERAL FUNCTIONS
	 ******************************************************/

	function getId(id) {
		if (id) {
			id = '#' + $.trim(id);
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
	
	function contains(array, element) {
		for (var i = 0; i < array.length; i++) {
			if (array[i] == element) {
				return true;
			}
		}
		return false;
	}
	
	function startsWith(string, str) {
		return string.match("^" + str) == str;
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
