function padLeadingZeros(number, width) {
	var tmp = '' + number;
	while (tmp.length < width) {
		tmp = '0' + tmp;
	}
	return tmp;
}

function integerToHexString(integer) {
	return padLeadingZeros(integer.toString(16).toUpperCase(), 4);
}

function generateCss(name, color, foregroundColor) {
	return '.' + name + ' { margin-left: -1px; margin-right: -1px; border: 1px solid ' + color + '; color: ' + color + '; }\n' + 
	'.' + name + '_hovered { background-color: ' + color + '; color: ' + foregroundColor + '; }\n';
}

function getForgroundColor(foregroundColor) {
	if (foregroundColor && foregroundColor != '') {
		return foregroundColor;
	}
	return '#000000';
}

function registerHovering(element) {
	$(element).on('mouseover', function(event) {
		$($(this).attr('class').split(' ')).each(function(index1, element1) {
			if (element1.indexOf('hover') == -1) {
				$('.' + element1).each(function (index2, element2) {
					$(element2).addClass(element1 + '_hovered');
				});
			}
		});
		event.stopPropagation();
	});
	$(element).on('mouseout', function(event) {
		$($(this).attr('class').split(' ')).each(function(index1, element1) {
			if (element1.indexOf('hover') == -1) {
				$('.' + element1).each(function (index2, element2) {
					$(element2).removeClass(element1 + '_hovered');
				});
			}
		});
		event.stopPropagation();
	});
}
