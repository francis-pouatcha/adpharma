// When the document is ready, initialize the link so
// that when it is clicked, the printable area of the
// page will print.
$(
function(){
 
// Hook up the print link.
$( "a#print" )
.attr( "href", "javascript:void( 0 )" )
.click(
function(){
// Print the DIV.
$( ".printable" ).print();
 
// Cancel click event.
return( false );
}
)
;
 
}
);