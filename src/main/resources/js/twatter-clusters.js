var w = screen.width,
    h = screen.height,
    fill = d3.scale.category20c(),
    format = d3.format(",d");

var packer = d3.layout.pack()
    .sort(null)
    .size([w, h]);

var vis = d3.select("#chart").append("svg:svg")
    .attr("width", w)
    .attr("height", h)
    .attr("class", "packed-bubbles")

var node = vis.selectAll("g.node")
    .data(packer({ children: twatter })
    .filter(function(d) { return !d.children; }))
  .enter().append("svg:g")
    .attr("class", "circle")
    .attr("transform", function(d) { return "translate(" + d.x + "," + d.y + ")"});

node.append("svg:title")
    .text(function(d) { return d.data.id + ":" + format(d.data.value); });

node.append("svg:circle")
    .attr("r", function(d) { return d.r; })
    .attr("fill", function(d) { return fill(Math.random()); });

node.append("svg:text")
    .attr("text-anchor", "middle")
    .attr("dy", ".3em")
    .text(function(d) { return d.data.terms; });
