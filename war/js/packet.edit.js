function deletePacket(packetKey) {
	if (confirm("Delete?")) {
		$.ajax({
			url: "packet_ajax",
			data: {
				"operation": "delete_packet",
				"packet_key": packetKey
			}
		}).done(function(data) {
			if (data == 'ok') {
				window.location.replace("/packets.jsp");
				$('#analysis_entry').dialog('close');
			}
		});
	}
	return false;
}


function deleteAnalysis(packetKey, analysisKey) {
	if (confirm("Delete?")) {
		$.ajax({
			url: "packet_ajax",
			data: {
				"operation": "delete_analysis",
				"packet_key": packetKey,
				"analysis_key": analysisKey
			}
		}).done(function(data) {
			if (data == 'ok') {
				window.location.replace("/packets.jsp");
				$('#analysis_entry').dialog('close');
			}
		});
	}
	return false;
}

function deleteAnalysisentry(packetKey, analysisKey, entryKey) {
	if (confirm("Delete?")) {
		$.ajax({
			url: "packet_ajax",
			data: {
				"operation": "delete_entry",
				"packet_key": packetKey,
				"analysis_key": analysisKey,
				"entry_key": entryKey
			}
		}).done(function(data) {
			if (data == 'ok') {
				// Reload page
				window.location.reload();
				$('#analysis_entry').dialog('close');
			}
		});
	}
	return false;
}