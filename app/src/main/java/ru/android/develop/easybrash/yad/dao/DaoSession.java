package ru.android.develop.easybrash.yad.dao;

import android.database.sqlite.SQLiteDatabase;

import java.util.Map;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.identityscope.IdentityScopeType;
import de.greenrobot.dao.internal.DaoConfig;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig categoryDaoConfig;
    private final DaoConfig itemDaoConfig;

    private final CategoryDao categoryDao;
    private final ItemDao itemDao;

    public DaoSession(SQLiteDatabase db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        categoryDaoConfig = daoConfigMap.get(CategoryDao.class).clone();
        categoryDaoConfig.initIdentityScope(type);

        itemDaoConfig = daoConfigMap.get(ItemDao.class).clone();
        itemDaoConfig.initIdentityScope(type);

        categoryDao = new CategoryDao(categoryDaoConfig, this);
        itemDao = new ItemDao(itemDaoConfig, this);

        registerDao(Category.class, categoryDao);
        registerDao(Item.class, itemDao);
    }
    
    public void clear() {
        categoryDaoConfig.getIdentityScope().clear();
        itemDaoConfig.getIdentityScope().clear();
    }

    public CategoryDao getCategoryDao() {
        return categoryDao;
    }

    public ItemDao getItemDao() {
        return itemDao;
    }

}