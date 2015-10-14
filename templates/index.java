<html>
<head>
	<title>Flask Demo</title>
	<script type="text/javascript" src="./static/lib/d3.js"></script>
	<script type="text/javascript" src="./static/lib/leaflet.js"></script>
	<link rel="stylesheet" href="./static/lib/leaflet.css" />

	<style>
		body {
		    padding: 0;
		    margin: 0;
		}
		html, body, #map {
		    height: 100%;
		}
		circle {
			fill-opacity: .3;
			fill: red;
			stroke-width: 0px;
		}
	</style>

	<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no" />

</head>
<body>

	<div id="map"></div>
	
	<div class="tooltip" style="position: absolute; z-index: 10: top: 100px: left: 100px">
	   <p class="tooltip-text" id="title">This is the title</p>
	   <p class="tooltip-text" id="price">This is the price</p>
        </div>

	<script type="text/javascript">
		var map = L.map('map').setView([22.539029, 114.062076], 16);
		//this is the OpenStreetMap tile implementation
		//L.tileLayer('http://{s}.tile.osm.org/{z}/{x}/{y}.png', {
    		//attribution: '&copy; <a href="http://osm.org/copyright">OpenStreetMap</a> contributors'
		//}).addTo(map);
		//uncomment for Mapbox implementation, and supply your own access token
		L.tileLayer('https://api.tiles.mapbox.com/v4/{mapid}/{z}/{x}/{y}.png?access_token={accessToken}', {
			attribution: 'Map data &copy; <a href="http://openstreetmap.org">OpenStreetMap</a>, Imagery © <a href="http://mapbox.com">Mapbox</a>',
			mapid: 'mapbox.light',
			accessToken: pk.eyJ1IjoidG9kb2dlb3JnZSIsImEiOiJjaWYzYTY0dWcyZnVwc3ZtMm5mZnFlbmV1In0.tsHv6drEjlIEd6Y3T0UoeQ
		}).addTo(map);
		//create variables to store a reference to svg and g elements
		var svg = d3.select(map.getPanes().overlayPane).append("svg");
		var g = svg.append("g").attr("class", "leaflet-zoom-hide");
		function projectPoint(lat, lng) {
			return map.latLngToLayerPoint(new L.LatLng(lat, lng));
		}
		function projectStream(lat, lng) {
			var point = projectPoint(lat,lng);
			this.stream.point(point.x, point.y);
		}
		var transform = d3.geo.transform({point: projectStream});
		var path = d3.geo.path().projection(transform);
	  	d3.json("/getData/", function(data) {
			//create placeholder circle geometry and bind it to data
			var circles = g.selectAll("circle").data(data.features);
			circles.enter()
				.append("circle")
			// function to update the data
			function update() {
				// get bounding box of data
			    var bounds = path.bounds(data),
			        topLeft = bounds[0],
			        bottomRight = bounds[1];
			    var buffer = 50;
			    // reposition the SVG to cover the features.
			    svg .attr("width", bottomRight[0] - topLeft[0] + (buffer * 2))
			        .attr("height", bottomRight[1] - topLeft[1] + (buffer * 2))
			        .style("left", (topLeft[0] - buffer) + "px")
			        .style("top", (topLeft[1] - buffer) + "px");
			    g   .attr("transform", "translate(" + (-topLeft[0] + buffer) + "," + (-topLeft[1] + buffer) + ")");
			    // update circle position and size
			    circles
			    	.attr("cx", function(d) { return projectPoint(d.geometry.coordinates[0], d.geometry.coordinates[1]).x; })
			    	.attr("cy", function(d) { return projectPoint(d.geometry.coordinates[0], d.geometry.coordinates[1]).y; })
	    			.attr("r", function(d) { return Math.pow(d.properties.price,.3); });
			};
			// call function to 
			update();
			map.on("viewreset", update);
		});
	</script>

</body>
</html>