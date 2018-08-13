/*
 * Copyright (c) 2002-2018, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.workflow.modules.tipiforms.business.task;

import fr.paris.lutece.plugins.workflow.modules.tipi.service.TipiPlugin;
import fr.paris.lutece.plugins.workflowcore.business.config.ITaskConfigDAO;
import fr.paris.lutece.util.sql.DAOUtil;

/**
 * This class provides Data Access methods for {@link TaskTipiFormsProviderConfig} objects
 */
public class TaskTipiFormsProviderConfigDAO implements ITaskConfigDAO<TaskTipiFormsProviderConfig>
{
    // SQL QUERY
    private static final String SQL_QUERY_INSERT = " INSERT INTO workflow_task_tipiformsprovider_cf ( id_task,  id_form, id_question_refdet, id_question_amount, id_question_email )  VALUES ( ?,?,?,?,? ) ";
    private static final String SQL_QUERY_FIND_BY_PRIMARY_KEY = " SELECT id_task, id_form, id_question_refdet, id_question_amount, id_question_email FROM workflow_task_tipiformsprovider_cf  WHERE id_task = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE workflow_task_tipiformsprovider_cf SET id_form = ?, id_question_refdet = ?, id_question_amount = ?, id_question_email = ?  WHERE id_task = ? ";
    private static final String SQL_QUERY_DELETE = " DELETE FROM workflow_task_tipiformsprovider_cf WHERE id_task = ? ";

    /**
     * {@inheritDoc}
     */
    @Override
    public void insert( TaskTipiFormsProviderConfig config )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, TipiPlugin.getPlugin( ) );

        int nIndex = 0;

        daoUtil.setInt( ++nIndex, config.getIdTask( ) );

        objectToData( nIndex, config, daoUtil );

        daoUtil.executeUpdate( );
        daoUtil.close( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TaskTipiFormsProviderConfig load( int nIdTask )
    {
        TaskTipiFormsProviderConfig config = null;
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_BY_PRIMARY_KEY, TipiPlugin.getPlugin( ) );

        daoUtil.setInt( 1, nIdTask );

        daoUtil.executeQuery( );

        if ( daoUtil.next( ) )
        {
            config = dataToObject( daoUtil );
        }

        daoUtil.close( );

        return config;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void store( TaskTipiFormsProviderConfig config )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, TipiPlugin.getPlugin( ) );

        int nIndex = 0;
        nIndex = objectToData( nIndex, config, daoUtil );

        daoUtil.setInt( ++nIndex, config.getIdTask( ) );

        daoUtil.executeUpdate( );
        daoUtil.close( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete( int nIdTask )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, TipiPlugin.getPlugin( ) );

        daoUtil.setInt( 1, nIdTask );
        daoUtil.executeUpdate( );
        daoUtil.close( );
    }

    /**
     * Creates a {@code TaskTipiFormsProviderConfig} object from the data of the specified {@code DAOUtil}
     * 
     * @param daoUtil
     *            the {@code DAOUtil} containing the data
     * @return a new {@code TaskTipiFormsProviderConfig} object
     */
    private TaskTipiFormsProviderConfig dataToObject( DAOUtil daoUtil )
    {
        TaskTipiFormsProviderConfig config = new TaskTipiFormsProviderConfig( );

        config.setIdTask( daoUtil.getInt( "id_task" ) );
        config.setIdForm( daoUtil.getInt( "id_form" ) );
        config.setIdRefDetQuestion( daoUtil.getInt( "id_question_refdet" ) );
        config.setIdAmountQuestion( daoUtil.getInt( "id_question_amount" ) );
        config.setIdEmailQuestion( daoUtil.getInt( "id_question_email" ) );

        return config;
    }

    /**
     * Populates the specified {@code DAOUtil} with the specified {@code TaskTipiFormsProviderConfig} object
     * 
     * @param nStartIndex
     *            the start index
     * @param config
     *            the {@code TaskTipiFormsProviderConfig} object
     * @param daoUtil
     *            the {@code DAOUtil} to populate
     * @return the last used index
     */
    private int objectToData( int nStartIndex, TaskTipiFormsProviderConfig config, DAOUtil daoUtil )
    {
        int nIndex = nStartIndex;

        daoUtil.setInt( ++nIndex, config.getIdForm( ) );
        daoUtil.setInt( ++nIndex, config.getIdRefDetQuestion( ) );
        daoUtil.setInt( ++nIndex, config.getIdAmountQuestion( ) );
        daoUtil.setInt( ++nIndex, config.getIdEmailQuestion( ) );

        return nIndex;
    }

}
