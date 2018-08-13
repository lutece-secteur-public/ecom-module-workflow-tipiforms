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
package fr.paris.lutece.plugins.workflow.modules.tipiforms.web.task;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import fr.paris.lutece.plugins.forms.business.FormHome;
import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.forms.business.QuestionHome;
import fr.paris.lutece.plugins.forms.business.Step;
import fr.paris.lutece.plugins.forms.business.StepHome;
import fr.paris.lutece.plugins.workflow.modules.tipiforms.business.task.TaskTipiFormsProviderConfig;
import fr.paris.lutece.plugins.workflowcore.service.config.ITaskConfigService;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.util.ReferenceItem;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.html.HtmlTemplate;

/**
 * This class represents a controller for the configuration of the task
 * {@link fr.paris.lutece.plugins.workflow.modules.tipiforms.service.task.TipiFormsProviderTask TipiFormsProviderTask}
 *
 */
public class TipiFormsProviderTaskConfigController
{
    // Parameters
    private static final String PARAMETER_APPLY = "apply";

    // Actions
    private static final String ACTION_CHANGE_FORM = "changeForm";
    private static final String ACTION_DEFAULT = "defaultAction";

    // Beans
    private static final String SERVICE_CONFIG = "workflow-tipiforms.taskTipiFormsProviderConfigService";

    // Services
    private static final ITaskConfigService _taskConfigService = SpringContextService.getBean( SERVICE_CONFIG );

    // Other constants
    private static final int ID_UNSET = -1;

    private final ITask _task;
    private TaskTipiFormsProviderConfig _config;

    /**
     * Constructor
     * 
     * @param task
     *            the task associated to the configuration
     */
    public TipiFormsProviderTaskConfigController( ITask task )
    {
        _task = task;

        findConfig( );
    }

    /**
     * Finds the configuration
     */
    private void findConfig( )
    {
        _config = _taskConfigService.findByPrimaryKey( _task.getId( ) );

        if ( _config == null )
        {
            // no configuration stored yet for this task, setting a empty one
            _config = new TaskTipiFormsProviderConfig( );
        }
    }

    /**
     * Builds the view depending on the specified request and locale
     * 
     * @param request
     *            the request the locale
     * @return the {@link HtmlTemplate} representing the view
     */
    public HtmlTemplate buildView( HttpServletRequest request )
    {
        return new View( request ).build( );
    }

    /**
     * Performs an action triggered by the user
     * 
     * @param request
     *            the request containing the action
     * @return the URL of the error page if there is an error during the action, {@code null} otherwise
     */
    public String performAction( HttpServletRequest request )
    {
        String strErrorUrl = null;
        Action action = new Action( request );
        String strAction = findAction( action, request );

        if ( ACTION_CHANGE_FORM.equals( strAction ) )
        {
            strErrorUrl = action.changeForm( );
        }

        if ( ACTION_DEFAULT.equals( strAction ) )
        {
            strErrorUrl = action.save( );
        }

        return strErrorUrl;
    }

    /**
     * Finds the action depending on the specified action and request
     * 
     * @param action
     *            the action
     * @param request
     *            the request
     * @return the action
     */
    private String findAction( Action action, HttpServletRequest request )
    {
        String strAction = request.getParameter( PARAMETER_APPLY );

        if ( ACTION_CHANGE_FORM.equals( strAction ) || action.isFormChanged( ) )
        {
            strAction = ACTION_CHANGE_FORM;
        }

        if ( StringUtils.isBlank( strAction ) )
        {
            strAction = ACTION_DEFAULT;
        }

        return strAction;
    }

    /**
     * This class represents a view of the task configuration
     *
     */
    private final class View
    {
        // MARKS
        private static final String MARK_CONFIG = "config";
        private static final String MARK_LIST_FORM = "list_form";
        private static final String MARK_LIST_QUESTION = "list_question";

        // TEMPLATE
        private static final String TEMPLATE_TASK_CONFIG = "admin/plugins/workflow/modules/tipiforms/tipiformsprovider_task_config.html";

        // Other constants
        private static final String NAME_SEPARATOR = " - ";

        private final HttpServletRequest _request;
        private final Map<String, Object> _model;
        private final ReferenceItem _referenceItemEmpty;

        /**
         * Constructor
         * 
         * @param request
         *            the request used by the view
         */
        View( HttpServletRequest request )
        {
            _request = request;
            _model = new HashMap<String, Object>( );
            _referenceItemEmpty = new ReferenceItem( );
            _referenceItemEmpty.setCode( Integer.toString( ID_UNSET ) );
            _referenceItemEmpty.setName( StringUtils.EMPTY );
        }

        /**
         * Builds the first step of the configuration
         * 
         * @return the {@link HtmlTemplate} representing the view
         */
        private HtmlTemplate build( )
        {
            _model.put( MARK_CONFIG, _config );
            _model.put( MARK_LIST_FORM, findForms( ) );
            _model.put( MARK_LIST_QUESTION, findQuestions( ) );

            return AppTemplateService.getTemplate( TEMPLATE_TASK_CONFIG, _request.getLocale( ), _model );
        }

        /**
         * Finds the forms
         * 
         * @return the forms as a {@code ReferenceList}
         */
        private ReferenceList findForms( )
        {
            ReferenceList referenceList = createReferenceListWithEmptyElement( );
            referenceList.addAll( FormHome.getFormsReferenceList( ) );

            return referenceList;
        }

        /**
         * Creates a {@code ReferenceList} with an empty element
         * 
         * @return the {@code ReferenceList}
         */
        private ReferenceList createReferenceListWithEmptyElement( )
        {
            ReferenceList referenceList = new ReferenceList( );
            referenceList.add( _referenceItemEmpty );

            return referenceList;
        }

        /**
         * finds the questions for the selected form
         * 
         * @return the questions as a {@code ReferenceList}
         */
        private ReferenceList findQuestions( )
        {
            ReferenceList referenceList = createReferenceListWithEmptyElement( );

            for ( Step step : StepHome.getStepsListByForm( _config.getIdForm( ) ) )
            {
                for ( Question question : QuestionHome.getQuestionsListByStep( step.getId( ) ) )
                {
                    referenceList.addItem( question.getId( ), buildNameForQuestion( step, question ) );
                }
            }

            return referenceList;
        }

        /**
         * Builds the displayed name for the questions
         * 
         * @param step
         *            the step of the question
         * @param question
         *            the question
         * @return the displayed name
         */
        private String buildNameForQuestion( Step step, Question question )
        {
            StringBuilder sbName = new StringBuilder( step.getTitle( ) );
            sbName.append( NAME_SEPARATOR ).append( question.getTitle( ) );

            return sbName.toString( );
        }
    }

    /**
     * This class represents an action in the task configuration
     *
     */
    private final class Action
    {
        // Messages
        private static final String MESSAGE_MANDATORY_FORM = "module.workflow.tipiforms.task_tipiformsprovider_config.message.mandatory.form";
        private static final String MESSAGE_MANDATORY_QUESTION_REFDET = "module.workflow.tipiforms.task_tipiformsprovider_config.message.mandatory.question.refdet";
        private static final String MESSAGE_MANDATORY_QUESTION_AMOUNT = "module.workflow.tipiforms.task_tipiformsprovider_config.message.mandatory.question.amount";
        private static final String MESSAGE_MANDATORY_QUESTION_EMAIL = "module.workflow.tipiforms.task_tipiformsprovider_config.message.mandatory.question.email";

        // Parameters
        private static final String PARAMETER_FORM_ID = "idForm";
        private static final String PARAMETER_QUESTION_REFDET_ID = "idRefDetQuestion";
        private static final String PARAMETER_QUESTION_AMOUNT_ID = "idAmountQuestion";
        private static final String PARAMETER_QUESTION_EMAIL_ID = "idEmailQuestion";

        private final HttpServletRequest _request;
        private final TaskTipiFormsProviderConfig _configFromRequest;

        /**
         * Constructor
         * 
         * @param request
         *            the request used to perform the action
         */
        Action( HttpServletRequest request )
        {
            _request = request;
            _configFromRequest = createConfigFrom( _request );

        }

        /**
         * Creates a configuration from the specified request
         * 
         * @param request
         *            the request
         * @return the created configuration
         */
        private TaskTipiFormsProviderConfig createConfigFrom( HttpServletRequest request )
        {
            TaskTipiFormsProviderConfig config = new TaskTipiFormsProviderConfig( );
            config.setIdTask( _task.getId( ) );
            config.setIdForm( NumberUtils.toInt( _request.getParameter( PARAMETER_FORM_ID ), ID_UNSET ) );
            config.setIdRefDetQuestion( NumberUtils.toInt( request.getParameter( PARAMETER_QUESTION_REFDET_ID ), ID_UNSET ) );
            config.setIdAmountQuestion( NumberUtils.toInt( request.getParameter( PARAMETER_QUESTION_AMOUNT_ID ), ID_UNSET ) );
            config.setIdEmailQuestion( NumberUtils.toInt( request.getParameter( PARAMETER_QUESTION_EMAIL_ID ), ID_UNSET ) );

            return config;
        }

        /**
         * Tests whether the user has changed the form
         * 
         * @return {@code true} if the form has changed, {@code false} otherwise
         */
        private boolean isFormChanged( )
        {
            return _configFromRequest.getIdForm( ) != _config.getIdForm( );
        }

        /**
         * Performs the actions when the form has changed
         * 
         * @return the URL of the error page if there is an error during the action, {@code null} otherwise
         */
        private String changeForm( )
        {
            String strErrorUrl = validateFormIsSet( );

            if ( !StringUtils.isEmpty( strErrorUrl ) )
            {
                return strErrorUrl;
            }

            fillConfigForNewForm( );
            saveConfig( );

            return null;
        }

        /**
         * Saves the configuration
         * 
         * @return the URL of the error page if there is an error during the action, {@code null} otherwise
         */
        private String save( )
        {
            String strResult = validateAllConfig( );

            if ( !StringUtils.isEmpty( strResult ) )
            {
                return strResult;
            }

            fillAllConfig( );
            saveConfig( );

            return null;

        }

        /**
         * Validates that the form is set
         * 
         * @return the URL of the error page if there is a validation error, {@code null} otherwise
         */
        private String validateFormIsSet( )
        {
            String strErrorUrl = null;

            if ( _configFromRequest.getIdForm( ) == ID_UNSET )
            {
                strErrorUrl = buildUrlForMissingForm( );
            }

            return strErrorUrl;
        }

        /**
         * Validates all the attribute of the configuration
         * 
         * @return the URL of the error page if there is a validation error, {@code null} otherwise
         */
        private String validateAllConfig( )
        {
            String strErrorUrl = validateFormIsSet( );

            if ( !StringUtils.isEmpty( strErrorUrl ) )
            {
                return strErrorUrl;
            }

            strErrorUrl = validateRefDetQuestionIsSet( );

            if ( !StringUtils.isEmpty( strErrorUrl ) )
            {
                return strErrorUrl;
            }

            strErrorUrl = validateAmountQuestionIsSet( );

            if ( !StringUtils.isEmpty( strErrorUrl ) )
            {
                return strErrorUrl;
            }

            strErrorUrl = validateEmailQuestionIsSet( );

            if ( !StringUtils.isEmpty( strErrorUrl ) )
            {
                return strErrorUrl;
            }

            return null;
        }

        /**
         * Validates that the RefDet question is set
         * 
         * @return the URL of the error page if there is a validation error, {@code null} otherwise
         */
        private String validateRefDetQuestionIsSet( )
        {
            String strErrorUrl = null;

            if ( _configFromRequest.getIdRefDetQuestion( ) == ID_UNSET )
            {
                strErrorUrl = buildUrlForMissingRefDetQuestion( );
            }

            return strErrorUrl;
        }

        /**
         * Validates that the amount question is set
         * 
         * @return the URL of the error page if there is a validation error, {@code null} otherwise
         */
        private String validateAmountQuestionIsSet( )
        {
            String strErrorUrl = null;

            if ( _configFromRequest.getIdAmountQuestion( ) == ID_UNSET )
            {
                strErrorUrl = buildUrlForMissingAmountQuestion( );
            }

            return strErrorUrl;
        }

        /**
         * Validates that the email question is set
         * 
         * @return the URL of the error page if there is a validation error, {@code null} otherwise
         */
        private String validateEmailQuestionIsSet( )
        {
            String strErrorUrl = null;

            if ( _configFromRequest.getIdEmailQuestion( ) == ID_UNSET )
            {
                strErrorUrl = buildUrlForMissingEmailQuestion( );
            }

            return strErrorUrl;
        }

        /**
         * Builds the URL for missing form
         * 
         * @return the URL
         */
        private String buildUrlForMissingForm( )
        {
            return AdminMessageService.getMessageUrl( _request, MESSAGE_MANDATORY_FORM, AdminMessage.TYPE_STOP );
        }

        /**
         * Builds the URL for missing RefDet question
         * 
         * @return the URL
         */
        private String buildUrlForMissingRefDetQuestion( )
        {
            return AdminMessageService.getMessageUrl( _request, MESSAGE_MANDATORY_QUESTION_REFDET, AdminMessage.TYPE_STOP );
        }

        /**
         * Builds the URL for missing amount question
         * 
         * @return the URL
         */
        private String buildUrlForMissingAmountQuestion( )
        {
            return AdminMessageService.getMessageUrl( _request, MESSAGE_MANDATORY_QUESTION_AMOUNT, AdminMessage.TYPE_STOP );
        }

        /**
         * Builds the URL for missing email question
         * 
         * @return the URL
         */
        private String buildUrlForMissingEmailQuestion( )
        {
            return AdminMessageService.getMessageUrl( _request, MESSAGE_MANDATORY_QUESTION_EMAIL, AdminMessage.TYPE_STOP );
        }

        /**
         * Fills the configuration when a new form is selected
         */
        private void fillConfigForNewForm( )
        {
            if ( isFormChanged( ) )
            {
                fillConfigWithForm( );
                fillConfigWithUnsetQuestions( );
            }
        }

        /**
         * Fills the configuration with the form
         */
        private void fillConfigWithForm( )
        {
            _config.setIdForm( _configFromRequest.getIdForm( ) );
        }

        /**
         * Fills the configuration with the unset id for questions
         */
        private void fillConfigWithUnsetQuestions( )
        {
            _config.setIdRefDetQuestion( ID_UNSET );
            _config.setIdAmountQuestion( ID_UNSET );
            _config.setIdEmailQuestion( ID_UNSET );
        }

        /**
         * Fills the configuration with all the attributes
         */
        private void fillAllConfig( )
        {
            _config = _configFromRequest;
        }

        /**
         * Saves the configuration of the task
         */
        private void saveConfig( )
        {
            boolean bCreate = false;

            if ( _config.getIdTask( ) == 0 )
            {
                _config.setIdTask( _task.getId( ) );
                bCreate = true;
            }

            if ( bCreate )
            {
                _taskConfigService.create( _config );
            }
            else
            {
                _taskConfigService.update( _config );
            }
        }
    }
}
