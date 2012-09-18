// When the DOM is ready, initialize the scripts.

jQuery(function( $ ){
	
 
// Get a reference to the container.
var container = $( "#container" );
 
 
// Bind the link to toggle the slide.
$( "a.aide" ).click(
function( event ){
// Prevent the default event.
event.preventDefault();
 
// Toggle the slide based on its current
// visibility.
if (container.is( ":visible" )){
 
// Hide - slide up.
container.slideUp( 1000 );
 
} else {
 
// Show - slide down.
container.slideDown( 1000 );
 
}
}
);
$('img.loadimage').ajaxStart(function(){
	$(this).show();
});

$('img.loadimage').ajaxStop(function(){
	$(this).hide();
});
 
});
 
