import React from 'react'
import './Header.scss';
import { Link } from 'react-router-dom';

export const Header = () => {

    return (
        <div className="header">
            <h2> SnapCrescent</h2>
            <ul className="header-list">
                <Link to="/photos">
                    <li>
                        Photos
                    </li>
                </Link>
                <Link to="favourites">
                    <li>
                        Favourites
                    </li>
                </Link>
                <Link to="/videos">
                    <li>
                        Videos
                    </li>
                </Link>
            </ul>
        </div>
    )
}