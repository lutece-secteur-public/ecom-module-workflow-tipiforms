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
package fr.paris.lutece.plugins.workflow.modules.tipiforms.service.task;

import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import fr.paris.lutece.plugins.forms.business.FormQuestionResponse;
import fr.paris.lutece.plugins.forms.business.FormQuestionResponseHome;
import fr.paris.lutece.plugins.forms.business.FormResponse;
import fr.paris.lutece.plugins.forms.business.FormResponseHome;
import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.forms.business.QuestionHome;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.workflow.modules.tipi.service.ITipiRefDetHistoryService;
import fr.paris.lutece.plugins.workflow.modules.tipi.service.ITipiService;
import fr.paris.lutece.plugins.workflow.modules.tipi.service.task.AbstractTipiProviderTask;
import fr.paris.lutece.plugins.workflow.modules.tipiforms.business.task.TaskTipiFormsProviderConfig;
import fr.paris.lutece.plugins.workflow.modules.tipiforms.business.task.TaskTipiFormsProviderConfigDAO;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceHistory;
import fr.paris.lutece.plugins.workflowcore.service.resource.IResourceHistoryService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.util.AppException;

/**
 * 
 * This class is a {@code Task} which provides pieces of information for the TIPI transaction from a form
 *
 */
public class TipiFormsProviderTask extends AbstractTipiProviderTask
{
    // Message
    private static final String MESSAGE_TASK_TITLE = "module.workflow.tipiforms.task_forms_provider_title";

    private final TaskTipiFormsProviderConfigDAO _taskTipiFormsProviderConfigDAO;

    /**
     * Constructor
     * 
     * @param taskTipiFormsProviderConfigDAO
     *            the configuration DAO
     * @param resourceHistoryService
     *            the resource history service
     * @param tipiService
     *            the tipi service
     * @param tipiRefDetHistoryService
     *            the RefDet history service
     */
    @Inject
    public TipiFormsProviderTask( TaskTipiFormsProviderConfigDAO taskTipiFormsProviderConfigDAO, IResourceHistoryService resourceHistoryService,
            ITipiService tipiService, ITipiRefDetHistoryService tipiRefDetHistoryService )
    {
        super( resourceHistoryService, tipiService, tipiRefDetHistoryService );

        _taskTipiFormsProviderConfigDAO = taskTipiFormsProviderConfigDAO;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTitle( Locale local )
    {
        return I18nService.getLocalizedString( MESSAGE_TASK_TITLE, local );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String provideRefDet( ResourceHistory resourceHistory )
    {
        FormResponse formResponse = findFormResponseFrom( resourceHistory );
        TaskTipiFormsProviderConfig config = findConfig( );

        return findResponseValue( formResponse, config.getIdRefDetQuestion( ) );
    }

    /**
     * Finds a form response from the specified resource history
     * 
     * @param resourceHistory
     *            the resource history
     * @return the form response
     */
    private FormResponse findFormResponseFrom( ResourceHistory resourceHistory )
    {
        FormResponse formResponse = null;

        if ( FormResponse.RESOURCE_TYPE.equals( resourceHistory.getResourceType( ) ) )
        {
            formResponse = FormResponseHome.findByPrimaryKey( resourceHistory.getIdResource( ) );
        }
        else
        {
            throw new AppException( "This task must be used with a form" );
        }

        return formResponse;
    }

    /**
     * Finds the configuration linked to this task
     * 
     * @return the configuration
     */
    private TaskTipiFormsProviderConfig findConfig( )
    {
        return _taskTipiFormsProviderConfigDAO.load( getId( ) );
    }

    /**
     * Finds the response value in the specified form response by using the specified question id
     * 
     * @param formResponse
     *            the form response
     * @param nIdQuestion
     *            the question id
     * @return the response value
     */
    private String findResponseValue( FormResponse formResponse, int nIdQuestion )
    {
        String strValue = StringUtils.EMPTY;
        boolean bFound = false;

        for ( FormQuestionResponse formQuestionResponse : FormQuestionResponseHome.getFormQuestionResponseListByFormResponse( formResponse.getId( ) ) )
        {
            Question question = QuestionHome.findByPrimaryKey( formQuestionResponse.getIdQuestion( ) );

            if ( question.getId( ) == nIdQuestion )
            {
                List<Response> listResponse = formQuestionResponse.getEntryResponse( );

                if ( listResponse.size( ) > 1 )
                {
                    throw new AppException( "The question contains several responses !" );
                }

                strValue = listResponse.get( 0 ).getToStringValueResponse( );
                bFound = true;
            }
        }

        if ( !bFound )
        {
            throw new AppException( "The question with the following id does not exist : " + nIdQuestion );
        }

        return strValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected int provideAmount( ResourceHistory resourceHistory )
    {
        FormResponse formResponse = findFormResponseFrom( resourceHistory );
        TaskTipiFormsProviderConfig config = findConfig( );

        return NumberUtils.toInt( findResponseValue( formResponse, config.getIdAmountQuestion( ) ) );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String provideEmail( ResourceHistory resourceHistory )
    {
        FormResponse formResponse = findFormResponseFrom( resourceHistory );
        TaskTipiFormsProviderConfig config = findConfig( );

        return findResponseValue( formResponse, config.getIdEmailQuestion( ) );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doRemoveConfig( )
    {
        _taskTipiFormsProviderConfigDAO.delete( getId( ) );
    }
}
