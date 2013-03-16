var actions = [];
var player = "player1";

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
     // Check 3 states
     // If it's a player, switch active player
     // If it's visible, target for active player
     // If it's not visible, do nothing
     if (cell.hasClass('player1')) {
       player = "player1";
     }
     else if (cell.hasClass('player2')) {
       player = "player2";
     } else if (cell.hasClass('visible')) {
       console.log("Moving character 1 to "+x+","+y);
       // TODO: We should untoggle the previous action
       $('#cell-'+x+'-'+y).toggleClass(player);
       actions.push({character:parseInt(player.substr(-1)), x:x, y:y})
     }
 })
});
