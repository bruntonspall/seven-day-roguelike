var actions = [];

$(function() {
  $('.execute').click(function() {
    $.post('/execute', { "actions": JSON.stringify(actions) }).done(function (data) {
      console.log(data);
      window.location.reload();
    });
  })
 $('.glyph').click( function (ev) {
     var cell = $(this);
     var x = cell.data('x');
     var y = cell.data('y');
     console.log("Moving character 1 to "+x+","+y);
     $('#cell-'+x+'-'+y).css({"color": "red"});
     actions = [{character:1, x:x, y:y}]
 })
});
