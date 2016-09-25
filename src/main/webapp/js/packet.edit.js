function getAnalysisEntryDialogContent(key, name, start, end, description, color, foregroundcolor) {
	var retval;
	retval = '<table>' +
		'<tr><th>Name*</th><td><input type="text" id="entry_name" value="' + name +'" style="width: 250px;" /><input type="hidden" id="entry_name_old" value="' + name +'" style="width: 250px;" /></td><td class="description">Unique name of this datapart within the packet (e.g. \'header0\')</td></tr>' +
		'<tr><th>Start / End</th><td><input type="text" id="entry_start" value="0x' + start + '" style="width: 67px;" /><input type="text" id="entry_end" value="0x' + end + '" style="margin-left: 10px; width: 67px;" /> (' + (parseInt(end, 16) - parseInt(start, 16) + 1) + ' Bytes)</td><td class="description">First and last byte of datapart in packet</td></tr>' +
		'<tr><th>Description*</th><td><textarea id="entry_description" rows="4" cols="30" style="width: 250px;">' + description +'</textarea></td><td class="description">Human readable description of this datapart</td></tr>' +
		'<tr><th>Color*</th><td><div class="ui-widget"><input type="text" id="entry_color" value="' + color +'" style="width: 250px;" /></div></td><td class="description">Color to be used for this datapart</td></tr>' +
		'<tr><th>Foregroundcolor</th><td><div class="ui-widget"><input type="text" id="entry_foregroundcolor" value="' + (foregroundcolor == undefined ? '' : foregroundcolor) +'" style="width: 250px;" /></div></td><td class="description">Foregroundcolor to be used for this datapart (optional)</td></tr>' +
		'<tr><td></td><td><input type="button" onclick="submitAnalysisEntry(this);" value="save" />';
	if (key && key != '') {
		retval += '<input type="hidden" id="entry_key" value="' + key + '" />'
	}
	retval += '</td><td></td></tr>' +
		'</table>';
	return retval;
}
