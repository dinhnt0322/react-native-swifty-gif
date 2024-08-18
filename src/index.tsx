import {ImageSourcePropType, requireNativeComponent} from 'react-native';
import React, {FC} from 'react';
import {Image, ViewProps} from 'react-native';

const SwiftyGifView = requireNativeComponent('SwiftyGifView');

const resolveAssetSource = Image.resolveAssetSource;

type SwiftyGifProps = {
  source: ImageSourcePropType;
  resizeMode?: string;
  isPaused?: boolean;
} & ViewProps;

const SwiftyGif: FC<SwiftyGifProps> = props => {
  const {source} = props;
  let resolvedSource = '';

  if (source?.uri) {
    resolvedSource = source.uri;
  } else if (typeof source === 'number') {
    const assetSource = resolveAssetSource(source);
    resolvedSource = assetSource.uri;
  } else if (typeof source === 'string') {
    resolvedSource = source;
  }

  return <SwiftyGifView {...props} source={resolvedSource} />;
};

export default SwiftyGif;
