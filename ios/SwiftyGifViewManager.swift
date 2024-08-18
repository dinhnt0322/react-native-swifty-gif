import SwiftyGif
import UIKit

@objc(SwiftyGifViewManager)
class SwiftyGifViewManager: RCTViewManager {
  
  override func view() -> UIView! {
  return SwiftyGifView()
  }
  
  @objc override static func requiresMainQueueSetup() -> Bool {
  return true
  }
}

class SwiftyGifView: UIView {
  
  private var gifImageView: UIImageView?
  
  override init(frame: CGRect) {
  super.init(frame: frame)
  }
  
  required init?(coder: NSCoder) {
  super.init(coder: coder)
  }
  
  @objc var source: NSString? {
  didSet {
    guard let source = source else { return }
    setupGifView(source: source as String)
  }
  }
  
  @objc var resizeMode: NSString? {
  didSet {
    guard let resizeMode = resizeMode else { return }
    updateResizeMode(resizeMode: resizeMode as String)
  }
  }
  
  @objc var paused: NSNumber? {
  didSet {
    guard let paused = paused else { return }
    updatePausedState(paused: RCTConvert.bool(paused))
  }
  }
  
  private func setupGifView(source: String) {
  DispatchQueue.main.async {
    if self.gifImageView == nil {
    let gifImageView = UIImageView()
    gifImageView.contentMode = .scaleAspectFit
    self.addSubview(gifImageView)
    self.gifImageView = gifImageView
    }
    
    if let url = URL(string: source), UIApplication.shared.canOpenURL(url) {
    self.gifImageView?.setGifFromURL(url)
    } else {
    do {
      let gifImage = try UIImage(gifName: source)
      self.gifImageView?.setGifImage(gifImage, loopCount: -1) // Loop forever
    } catch {
      print("Failed to load GIF: \(error.localizedDescription)")
    }
    }
  }
  }
  
  private func updateResizeMode(resizeMode: String) {
  DispatchQueue.main.async {
    switch resizeMode {
    case "cover":
    self.gifImageView?.contentMode = .scaleAspectFill
    case "contain":
    self.gifImageView?.contentMode = .scaleAspectFit
    case "stretch":
    self.gifImageView?.contentMode = .scaleToFill
    default:
    self.gifImageView?.contentMode = .scaleAspectFit
    }
  }
  }
  
  private func updatePausedState(paused: Bool) {
  DispatchQueue.main.async {
    if paused {
    self.gifImageView?.stopAnimatingGif()
    } else {
    self.gifImageView?.startAnimatingGif()
    }
  }
  }
  
  override func layoutSubviews() {
  super.layoutSubviews()
  gifImageView?.frame = self.bounds
  }
}
