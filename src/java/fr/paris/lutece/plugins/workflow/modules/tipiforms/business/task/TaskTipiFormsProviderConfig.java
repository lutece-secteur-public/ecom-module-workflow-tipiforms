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

import javax.validation.constraints.Min;
import fr.paris.lutece.plugins.workflowcore.business.config.TaskConfig;

/**
 * 
 * This class represents a configuration for the task {@link fr.paris.lutece.plugins.workflow.modules.tipiforms.service.task.TipiFormsProviderTask
 * TipiFormsProviderTask}
 * 
 */
public class TaskTipiFormsProviderConfig extends TaskConfig
{
    @Min( 1 )
    private int _nIdForm;
    @Min( 1 )
    private int _nIdRefDetQuestion;
    @Min( 1 )
    private int _nIdAmountQuestion;
    @Min( 1 )
    private int _nIdEmailQuestion;

    /**
     * Gives the form id
     * 
     * @return the id
     */
    public int getIdForm( )
    {
        return _nIdForm;
    }

    /**
     * Sets the form id
     *
     * @param idForm
     *            the form id to set
     */
    public void setIdForm( int idForm )
    {
        _nIdForm = idForm;
    }

    /**
     * Gives the id of the question for the RefDet
     * 
     * @return the id
     */
    public int getIdRefDetQuestion( )
    {
        return _nIdRefDetQuestion;
    }

    /**
     * Sets the id of the question for the RefDet
     *
     * @param idRefDetQuestion
     *            the id to set
     */
    public void setIdRefDetQuestion( int idRefDetQuestion )
    {
        _nIdRefDetQuestion = idRefDetQuestion;
    }

    /**
     * Gives the id of the question for the amount
     * 
     * @return the id
     */
    public int getIdAmountQuestion( )
    {
        return _nIdAmountQuestion;
    }

    /**
     * Sets the id of the question for the amount
     *
     * @param idAmountQuestion
     *            the id to set
     */
    public void setIdAmountQuestion( int idAmountQuestion )
    {
        _nIdAmountQuestion = idAmountQuestion;
    }

    /**
     * Gives the id of the question for the email
     * 
     * @return the id
     */
    public int getIdEmailQuestion( )
    {
        return _nIdEmailQuestion;
    }

    /**
     * Sets the id of the question for the email
     *
     * @param idEmailQuestion
     *            the id to set
     */
    public void setIdEmailQuestion( int idEmailQuestion )
    {
        _nIdEmailQuestion = idEmailQuestion;
    }

}
