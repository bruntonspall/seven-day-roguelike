var actions = []

$(function() {
  $('.execute').click(function() {
    $.post('/execute', { "actions": [{character: 1, x:3, y:3}] }).done(function (data) {
      console.log(data);
      window.reload();
    });
  })
});
