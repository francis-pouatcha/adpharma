function showDetails(base,cBlock, i)
{ 
   if (base.animationStatus == false) {
    base.animationStatus = true;
    if (!base.clickStatus)
    {
      base.posXonClick = parseInt($(cBlock).css('left'));
      base.posYonClick = parseInt($(cBlock).css('top'));
      base.widthOnClick = parseInt($(cBlock).css('width'));
      base.heightOnClick = parseInt($(cBlock).children('.cImage').css('height'));
      base.opacityOnClick = $(cBlock).children('.cOverlay').css('opacity');
      base.scaleOnClick = base.image[i]["height"] / base.heightOnClick;
      
      base.stopCarousel();
      base.clickStatus = true;
      base.$el.find('.cImgTitle').css('display','none');
      $(cBlock).css('z-index', 2000);     
      $(cBlock).animate({
          left: base.options.detailsPadding,
          top: base.options.detailsPadding,
          //width: base.options.width - (2 * base.options.detailsPadding),
          width: base.image[i]["width"],
          height: base.image[i]["height"] + base.options.reflection
        }, base.options.animateSpeed, function() {
          base.animationStatus = false;  // wenn Animation fertig ist, kann wieder geclickt werden
      });

      $(cBlock).children('.cImage').animate({
          width: base.image[i]["width"],
          height: base.image[i]["height"]
        }, base.options.animateSpeed, function() {
      });
      
      $(cBlock).children('.cOverlay').animate({
          width: base.image[i]["width"],
          height: base.image[i]["height"],
          opacity: 0
        }, base.options.animateSpeed, function() {
      });
    
      if (base.options.reflection)
      {
        if (!$.support.scriptEval)
          $(cBlock).children('.cReflection').children().css('filter','flipv progid:DXImageTransform.Microsoft.Alpha(opacity='+base.options.reflectionStart*100 +', style=1, finishOpacity=0, startx=0, starty=0, finishx=0, finishy='+ base.options.reflection * 100 / base.image[i]["height"] +')');
        
        $(cBlock).children('.cReflection').animate({
            top: base.image[i]["height"],
            width: base.image[i]["width"],
            opacity: 1
          }, base.options.animateSpeed, function() {
        });
        
        $(cBlock).children('.cReflection').children().animate({
            width: base.image[i]["width"]
          }, base.options.animateSpeed, function() {
        }); 
        
      }
      $(cBlock).siblings('.cBlock').children('.cImage,.cReflection').animate({
        opacity: 0
        }, base.options.animateSpeed, function() {
          $(cBlock).siblings('.cBlock').css('display','none');
      });
      
      // ImageDescription
      var imgDescr = $('<div />').attr({'id':'imgDescr_'+base.el.id+''+(i+1), 'class':'imgDescr'});
      $(imgDescr).append(base.image[i]["description"]);
      imgDescr.css({
                            position : 'absolute',
                            left : base.posXonClick,
                            top : base.posYonClick,
                            'width' : base.options.width - base.image[i]["width"] - (3 * base.options.detailsPadding),
                            opacity : 0,
                            display: 'block'
                          });
      base.$el.find('.imgDescr').css({'display':'none','opacity':0}); //Bildbeschreibung für jedes Bild ausblenden
      base.$el.find('#carouselContent_'+base.el.id).append(imgDescr);
      base.$el.find('.imgDescr').animate({
        left: parseInt(base.image[i]["width"]) + 2*base.options.detailsPadding,
        top: base.options.detailsPadding,
        opacity: 1
        }, base.options.animateSpeed, function() {
      });
    }
    else
    {
      $(cBlock).siblings('.cBlock').css({'display':'block'});
    
      $(cBlock).siblings('.cBlock').children('.cImage,.cReflection').animate({
          opacity: 1
        }, base.options.animateSpeed, function() {
      });
                      
      $(cBlock).animate({  
          width: base.widthOnClick,
          height : base.heightOnClick + base.options.reflection / base.scaleOnClick,
          left: base.posXonClick,
          top: base.posYonClick
        }, base.options.animateSpeed, function() {
          base.clickStatus = false;
          base.animationStatus = false;  // wenn Animation fertig ist, kann wieder geclickt werden
          base.startCarousel();
      });
      
      $(cBlock).children('.cImage').animate({
          width: base.widthOnClick,
          height: base.heightOnClick
        }, base.options.animateSpeed, function() {
      });
      
      if (base.reflection)
      {
        $(cBlock).children('.cReflection').animate({
            width: base.widthOnClick,
            top: base.heightOnClick,
            opacity : 1 - base.opacityOnClick
          }, base.options.animateSpeed, function() {
            $(cBlock).children('.cReflection').children().css('width', base.widthOnClick);
        });
        
        $(cBlock).children('.cReflection').children().animate({
              width: base.widthOnClick
            }, base.options.animateSpeed, function() {
          });
      }
      
      $(cBlock).children('.cOverlay').animate({
          width: base.widthOnClick,
          height: base.heightOnClick + base.options.reflection,
          opacity: base.opacityOnClick
        }, base.options.animateSpeed, function() {
      });
      
      // Description
      base.$el.find('.imgDescr').animate({
          left: base.posXonClick,
          top: base.posYonClick,
          opacity : 0
        }, base.options.animateSpeed, function() {
          base.$el.find('.imgDescr').remove();
      });
    } // ende clickstatus
  } // ende animationStatus
}