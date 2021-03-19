import React from 'react';
import { StyleSheet, View } from 'react-native';
import CoreStyles from '../../styles/styles';
import Loader from '../Loader';
import { downloadPhotoById, getPhotoById } from '../../core/service/PhotoService';
import { Image } from 'react-native-elements';
import DropMenu from '../shared/drop-menu/DropMenu';
import { showErrorToast, showToast } from '../../core/service/ToastService';
import Share from 'react-native-share';
import { FILE_RESPONSE_TYPE } from '../../core/service/FileService';
import ImageViewer from 'react-native-image-zoom-viewer';

const ImageViewerModule = ImageViewer;

class PhotoSlide extends React.Component {

    photos = [];
    photoList = [];
    selectedPhotoId = null;
    photoRequestIntervalId = null;
    indexOfSelectedPhoto = 0;
    menuItems = [
        { label: 'Share', icon: 'share-alt', hasDivider: true, onPress: () => { this.sharePhoto(this.state.currentPhoto) } },
        { label: 'Download', icon: 'save', onPress: () => { this.downloadPhoto(this.state.currentPhoto) } }
    ];

    constructor(props) {
        super(props);
        this.photos = props.route?.params?.photos;
        this.selectedPhotoId = props.route?.params?.selectedPhotoId;

        this.photoList = this.photos.map(photo => {
            return {
                url: this.getPhotoURL(photo),
                props: {
                    ...photo
                }
            };
        });

        this.indexOfSelectedPhoto = this.photos.findIndex(photo => photo.id === this.selectedPhotoId);

        this.state = {
            currentPhoto: {},
            showLoader: false
        };
    }

    componentDidMount() {
        if (this.photos && this.selectedPhotoId) {
            this.getPhotoUriById(this.selectedPhotoId);
        }
    }

    componentDidUpdate() {
        if (this.state.currentPhoto?.label) {
            this.props.navigation.setOptions({
                title: this.state.currentPhoto?.label,
                headerRight: () => (
                    <DropMenu items={this.menuItems} />
                ),
            });
        }
    }

    getPhotoUriById(photoId) {
        if (photoId) {
            const indexOfPhoto = this.photos.findIndex(photo => photo.id == photoId);
            this.selectedPhotoId = photoId;
            const photo = {
                ...this.photos[indexOfPhoto],
                index: indexOfPhoto,
                label: this.getPhotoLabel(this.photos[indexOfPhoto])
            };

            if (this.photoRequestIntervalId) {
                clearInterval(this.photoRequestIntervalId);
            }

            this.setState({ currentPhoto: photo, showLoader: true }, () => {
                if (this.state.currentPhoto.source?.uri) {
                    this.setState({ ...this.state, showLoader: false });
                    return;
                }

                this.photoRequestIntervalId = setTimeout(() => {
                    getPhotoById(photoId)
                        .then((res) => {
                            if (res) {
                                if (photo.id == this.state.currentPhoto.id) {
                                    photo.imageSource = {
                                        uri: res
                                    };
                                    const selectedPhoto = this.photos.find(item => item.id == photo.id);
                                    selectedPhoto.imageSource = {
                                        uri: res
                                    };

                                    const selectedPhotoFromDisplayList = this.photoList.find(item => item.props.id == photo.id);
                                    selectedPhotoFromDisplayList.url = res;
                                    selectedPhotoFromDisplayList.props = {
                                        ...selectedPhoto
                                    };
                                    this.setState({ currentPhoto: { ...photo }, showLoader: false });
                                } else {
                                    this.setState({ ...this.state, showLoader: false });
                                }
                            }
                        })
                        .catch(error => {
                            showErrorToast();
                        });
                }, 1000);
            });

        } else {
            this.setState({ currentPhoto: null });
        }
    }

    getPhotoLabel(photo) {
        if (photo.createdDate) {
            return new Date(photo.createdDate).toDateString();
        } else {
            return 'Photo';
        }
    }

    handleSwipeEvent(index) {
        this.indexOfSelectedPhoto = index;
        const nextPhotoId = this.photos[index].id;
        this.getPhotoUriById(nextPhotoId);
    }

    sharePhoto(photo) {
        showToast(`Preparing ${photo.name} for sharing.`);
        this.setState({ ...this.state, showLoader: true });

        const params = {
            responseType: FILE_RESPONSE_TYPE.BASE64,
            mimeType: photo.mimeType
        };

        getPhotoById(photo.id, params)
            .then((res) => {
                this.setState({ ...this.state, showLoader: false });
                try {
                    Share.open({
                        filename: photo.name,
                        message: 'Shared by Snap Crescent.',
                        url: res,
                        title: photo.name,
                        type: photo.mimeType
                    }).then(() => {
                        showToast(`${photo.name} has been shared.`);
                    });
                } catch (error) {
                    showErrorToast();
                }
            }).catch(error => {
                showErrorToast();
            });
    }

    downloadPhoto(photo) {
        showToast(`Downloading ${photo.name}`);
        downloadPhotoById(photo.id, { name: photo.name }).then(res => {
            showToast(`${photo.name} has been downloaded.`);
        });
    }

    getPhotoURL(photo) {
        if (photo.imageSource && photo.imageSource.uri) {
            return photo.imageSource.uri
        } else {
            return photo.thumbnailSource.uri
        }
    }

    renderImage(props) {
        return (
            <View>
                {
                    props.imageSource?.uri
                        ? <Image source={props.imageSource} style={styles.image} PlaceholderContent={<Loader />} />
                        : <View>
                            <View style={CoreStyles.loader}><Loader /></View>
                            <Image source={props.thumbnailSource} style={styles.image} PlaceholderContent={<Loader />} />
                        </View>
                }
            </View>
        );
    }

    render() {
        return (
            <View style={styles.imageContainer}>
                <ImageViewerModule
                    renderImage={props => { return this.renderImage(props) }}
                    renderIndicator={props => null}
                    index={this.indexOfSelectedPhoto}
                    imageUrls={this.photoList}
                    onChange={(index) => { this.handleSwipeEvent(index) }}
                    backgroundColor={"#000a"}
                    saveToLocalByLongPress={false} />
            </View>
        );
    }
}

const styles = StyleSheet.create({
    imageContainer: {
        flex: 1
    },
    image: {
        width: '100%',
        height: '100%',
        resizeMode: 'contain'
    }
});

export default PhotoSlide;