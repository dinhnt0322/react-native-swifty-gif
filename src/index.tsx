import {
  ActivityIndicator,
  ImageSourcePropType,
  NativeSyntheticEvent,
  requireNativeComponent,
  StyleSheet,
  View,
} from 'react-native';
import React, {FC, useMemo} from 'react';
import {Image, ViewProps} from 'react-native';

const SwiftyGifView = requireNativeComponent('SwiftyGifView');

const resolveAssetSource = Image.resolveAssetSource;

export type GifLoadErrorEvent = NativeSyntheticEvent<{error: string}>;

export type SwiftyGifProps = ViewProps & {
  source: ImageSourcePropType;
  resizeMode?: string;
  paused?: boolean;
  onLoadGifEnd?: () => void;
  onLoadGifError?: (event: GifLoadErrorEvent) => void;
  LoadingView?: React.ReactNode;
};

const SwiftyGif: FC<SwiftyGifProps> = props => {
  const {source, paused = false, onLoadGifEnd, onLoadGifError, style} = props;
  const [isLoading, setIsLoading] = React.useState(true);
  let resolvedSource = '';

  if (source?.uri) {
    resolvedSource = source.uri;
  } else if (typeof source === 'number') {
    const assetSource = resolveAssetSource(source);
    resolvedSource = assetSource.uri;
  } else if (typeof source === 'string') {
    resolvedSource = source;
  }

  const handLoadGifEnd = () => {
    setIsLoading(false);
    onLoadGifEnd && onLoadGifEnd();
  };

  const handleLoadGifError = (event: GifLoadErrorEvent) => {
    setIsLoading(false);
    onLoadGifError && onLoadGifError(event);
  };

  const renderIndicator = useMemo(() => {
    if (!isLoading) {
      return null;
    }
    const defaultIndicatorSize = style
      ? Math.min(style?.width ?? 0, style?.height ?? 0) / 2
      : 'small';
    return (
      <View style={[style, styles.indicator]}>
        {props.LoadingView ? (
          props.LoadingView
        ) : (
          <ActivityIndicator size={defaultIndicatorSize} />
        )}
      </View>
    );
  }, [isLoading, props.LoadingView, style]);

  const onLoadGifStart = () => {
    setIsLoading(true);
  };

  return (
    <View style={[style, styles.container]}>
      <SwiftyGifView
        {...props}
        paused={paused}
        source={resolvedSource}
        onLoadGifStart={onLoadGifStart}
        onLoadGifEnd={handLoadGifEnd}
        onLoadGifError={handleLoadGifError}
      />
      {renderIndicator}
    </View>
  );
};

const styles = StyleSheet.create({
  indicator: {
    justifyContent: 'center',
    alignItems: 'center',
    position: 'absolute',
  },
  container: {
    overflow: 'hidden',
  },
});

export default SwiftyGif;
