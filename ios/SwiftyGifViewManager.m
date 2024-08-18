#import <React/RCTViewManager.h>

@interface RCT_EXTERN_MODULE(SwiftyGifViewManager, RCTViewManager)

RCT_EXPORT_VIEW_PROPERTY(source, NSString)

RCT_EXPORT_VIEW_PROPERTY(resizeMode, NSString)

RCT_EXPORT_VIEW_PROPERTY(paused, NSNumber)

@end
