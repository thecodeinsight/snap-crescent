import React from 'react';
import './Sidebar.scss';
import clsx from 'clsx';
import { makeStyles, useTheme } from '@material-ui/core/styles';
import Drawer from '@material-ui/core/Drawer';
import List from '@material-ui/core/List';
import Divider from '@material-ui/core/Divider';
import IconButton from '@material-ui/core/IconButton';
import ChevronLeftIcon from '@material-ui/icons/ChevronLeft';
import ChevronRightIcon from '@material-ui/icons/ChevronRight';
import ListItem from '@material-ui/core/ListItem';
import ListItemIcon from '@material-ui/core/ListItemIcon';
import ListItemText from '@material-ui/core/ListItemText';
import PhotoIcon from '@material-ui/icons/Photo';
import FavoriteIcon from '@material-ui/icons/Favorite';
import MovieIcon from '@material-ui/icons/Movie';
import { Header } from '../Header/Header';
import { Link } from 'react-router-dom';
import { useMediaQuery } from '@material-ui/core';
import FolderIcon from '@material-ui/icons/Folder';

const drawerWidth = 240;

const useStyles = makeStyles((theme) => ({
  drawer: {
    width: drawerWidth,
    flexShrink: 0,
    whiteSpace: 'nowrap',
  },
  drawerOpen: {
    width: drawerWidth,
    transition: theme.transitions.create('width', {
      easing: theme.transitions.easing.sharp,
      duration: theme.transitions.duration.enteringScreen,
    }),
    backgroundImage: `url(${process.env.PUBLIC_URL + '/sidebar.jpg'})`,
    backgroundSize: 'cover'
  },
  drawerClose: {
    transition: theme.transitions.create('width', {
      easing: theme.transitions.easing.sharp,
      duration: theme.transitions.duration.leavingScreen,
    }),
    overflowX: 'hidden',
    width: theme.spacing(7) + 1,
    [theme.breakpoints.up('sm')]: {
      width: theme.spacing(9) + 1,
    },
    backgroundImage: `url(${process.env.PUBLIC_URL + '/sidebar.jpg'})`,
    backgroundSize: 'cover'
  },
  toolbar: {
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'flex-end',
    padding: theme.spacing(0, 1),
    // necessary for content to be below app bar
    ...theme.mixins.toolbar,
  },
  listItemText: {
    color: '#fff',
    fontWeight: 'bold'
  },
  hide: {
    display: 'none'
  }
}));

export const Sidebar = () => {
  const classes = useStyles();
  const theme = useTheme();
  const isMobileView = useMediaQuery(theme.breakpoints.down('sm'));
  const [open, setOpen] = React.useState(false);

  const handleDrawerOpen = () => {
    setOpen(true);
  };

  const handleDrawerClose = () => {
    setOpen(false);
  };

  const sideNavItems = [
    {
      title: 'Photos',
      icon: <PhotoIcon />,
      url: '/home/photos'
    },
    {
      title: 'Favorites',
      icon: <FavoriteIcon />,
      url: '/home/favorites'
    },
    {
      title: 'Album',
      icon: <FolderIcon />,
      url: '/home/albums'
    },
    {
      title: 'Videos',
      icon: <MovieIcon />,
      url: '/home/videos'
    },
  ];
  return (
    <div>
      {/* <CssBaseline /> */}
      <Header open={open} onMenuClick={handleDrawerOpen} />
      <Drawer
        variant="permanent"
        className={clsx(classes.drawer, {
          [classes.drawerOpen]: open,
          [classes.drawerClose]: !open,
          [classes.hide]: isMobileView && !open
        })}
        classes={{
          paper: clsx({
            [classes.drawerOpen]: open,
            [classes.drawerClose]: !open,
            [classes.hide]: isMobileView && !open
          }),
        }}
      >
        <div className={classes.toolbar}>
          <IconButton onClick={handleDrawerClose}>
            {theme.direction === 'rtl' ? <ChevronRightIcon /> : <ChevronLeftIcon />}
          </IconButton>
        </div>
        <Divider />
        <List>
          {sideNavItems.map((item, index) => (
            <Link to={item.url} key={index} className="sidebar-link">
              <ListItem button>
                <ListItemIcon className='primary-color'>{item.icon}</ListItemIcon>
                <ListItemText classes={{primary: classes.listItemText}} primary={item.title} />
              </ListItem>
            </Link>
          ))}
        </List>
      </Drawer>
    </div>
  );
}
