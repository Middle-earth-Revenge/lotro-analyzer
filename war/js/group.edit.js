function createGroupDialog() {
	var create_group_dialog = $('#create_group_dialog');
	create_group_dialog.html(getCreateGroupDialog());
	$('a.button, input[type=submit], input[type=button]').button();
	create_group_dialog.dialog({
		title: 'Create group',
		width: 730,
		height: 220
	});
}

function getCreateGroupDialog() {
	var retval;
	retval = '<table>' +
		'<tr><th>Name*</th><td><input type="text" id="group_name" style="width: 250px;" /></td><td class="description">Unique name of this group of packets</td></tr>' +
		'<tr><th>Description</th><td><textarea id="group_description" rows="4" cols="30" style="width: 250px;"></textarea></td><td class="description">Human readable description of this group</td></tr>' +
		'<tr><td></td><td><input type="button" onclick="createGroup(this);" value="save" />';
	retval += '</td><td></td></tr>' +
		'</table>';
	return retval;
}

function createGroup(saveButton) {
	$(saveButton).attr('disabled', 'disabled');
	$('body').css('cursor', 'progress');
	$.ajax({
		url: "packet_ajax",
		data: {
			"operation": "create_group",
			"name": $('#group_name').val(),
			"description": $('#group_description').val()
		}
	}).done(function(data) {
		$(saveButton).removeAttr('disabled');
		$('body').css('cursor', 'auto');
		if (/^\d+$/.test(data)) {
			$('#group').append($('<option></option>').val(data).html($('#group_name').val()));
			$('#group').val(data);
			$('#create_group_dialog').dialog('close');
		}
	});
}
